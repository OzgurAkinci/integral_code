package com.app.integral_code.api;


import com.app.integral_code.dto.RequestDTO;
import com.app.integral_code.dto.ResponseDTO;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;

@Service
public class ApiService {
    public static PrintWriter pw = null;
    public static String latexFile = "PInteg";

    public ResponseDTO run (RequestDTO requestDTO) {
        int[] m = new int[requestDTO.getN()];
        for (int i = 0; i < requestDTO.getN(); i++) {
            m[i] = i;
        }
        Table table = new Table();
        double h = Math.PI/20;
        double y0 = Math.PI/5;
        table.put("h", new Ratio(h));
        for (int i=0; i<m.length; i++) {
            table.put("y"+i, new Ratio(Math.sin(y0+i*h)));
        }

        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder latexStringBuilder = new StringBuilder();

        return runMethod(stringBuilder, latexStringBuilder, table, m, requestDTO.isToText(), requestDTO.isToPdf());
    }

    public ResponseDTO runMethod(StringBuilder stringBuilder, StringBuilder latexStringBuilder, Table table, int[] m, boolean toText, boolean toPdf) {
        Tex_Create();
        var resp = processMethod(stringBuilder, latexStringBuilder, table, m, toText, toPdf);
        PutText(resp.getLatexFormat().toString());
        Tex_Close();

        if (toPdf) {
            resp = Tex_pdf(resp);
        }

        return resp;
    }

    public ResponseDTO processMethod(StringBuilder stringBuilder, StringBuilder latexStringBuilder, Table table, int[] m, boolean toText, boolean toPdf) {
        PInteg fd = new PInteg(m);

        if (toText) {
            stringBuilder.append("Sayisal Integral icin Polinom Yaklasim Formulleri").append("\n");
        }
        if (toPdf) {
            latexStringBuilder.append("\\section{Sayisal integral icin Polinom Yaklasim Formulleri}").append("\n");
        }

        //System.out.println("---1---");
        if (toText) {
            stringBuilder.append("Polinom Ifadesi").append("\n");
            stringBuilder.append("Bir Pn(x) polinomu genel olarak asagidaki ifade ile temsil edilir.").append("\n");
            stringBuilder.append("Pn(x) = c0+c1*x+c2*x^2+c3*x^3+...+cn*x^n").append("\n");
        }
        if (toPdf) {
            latexStringBuilder.append("\\subsection{Polinom ifadesi}").append("\n");
            latexStringBuilder.append("Bir $P_{n}(x)$ polinomu genel olarak asagidaki ifade ile temsil edilir.\\\\").append("\n");
            latexStringBuilder.append("$\\displaystyle P_{n}(x)= c_{0}+c_{1}x+c_{2}x^{2}+c_{3}x^{3}+\\cdot\\cdot\\cdot+c_{n}x^{n}$").append("\n");
        }

        //System.out.println("---2---");
        if (toText) {
            stringBuilder.append("Ornek Noktalar").append("\n");
            stringBuilder.append("Bu ifade xm=x0+mh biciminde secilen her bir nokta icin duzenlenir.").append("\n");
        }
        if (toPdf) {
            latexStringBuilder.append("\\subsection{Ornek Noktalar}").append("\n");
            latexStringBuilder.append("Bu ifade $x_{m}=x_{0}+mh$ biciminde secilen her bir nokta icin duzenlenir.\\\\").append("\n");
        }
        Texp[] e = fd.polynomial();
        for (int i=0; i<e.length; i++) {
            if (toText)
                stringBuilder.append(e[i]).append("\n");
            if (toPdf)
                latexStringBuilder.append("$\\displaystyle P_{").append(e.length - 1).append("}(").append(fd.mh[i]).append("*h)=").append(e[i].toLatex(false)).append("$\\\\").append("\n");
        }

        //System.out.println("---6---");
        //Exp[][] e5 = fd.coeffMatrix(e4);  //(1)
        stringBuilder.append("Class: ").append(e[2].toClass()).append("\n");
        if (toText) {
            stringBuilder.append("Denklem Sistemi").append("\n");
            stringBuilder.append("Bu denklemlerden katsayi matrisi olusturulur (h, h^{2} gibi terimler daha sonra cozume eklenecektir).").append("\n");
        }
        if (toPdf) {
            latexStringBuilder.append("\\subsection{Denklem Sistemi}").append("\n");
            latexStringBuilder.append("Bu denklemlerden katsayi matrisi olusturulur.").append("\n");
        }
        Exp[][] e5 = fd.coeffMatrix(e); //(2)
        if (toText)
            stringBuilder.append(fd.showMatrix(e5)).append("\n");
        if (toPdf)
            //PutMatrix(e5, false, "");
            latexStringBuilder = PutMatrix(latexStringBuilder, e5, false, "|");

        //System.out.println("---7---");
        if (toText)
            stringBuilder.append("Denklemler, uslu ifadeler hesaplanarak asagidaki gibi yeniden duzenlenir.").append("\n");
        if (toPdf)
            latexStringBuilder.append("Denklemler, uslu ifadeler hesaplanarak asagidaki gibi yeniden duzenlenir.").append("\n");
        Exp[][] e6 = fd.integMatrix(e5);      //(3)
        //Exp[][] e6 = fd.integMatrix(e5, d); //(4)
        if (toText)
            stringBuilder.append(fd.showMatrix(e6)).append("\n");
        if (toPdf)
            latexStringBuilder = PutMatrix(latexStringBuilder, e6, false, "|");

        //System.out.println("---8---");
        //Asagiya dogru indirge
        if (toText)
            stringBuilder.append("Denklem Cozumu").append("\n");
        if (toPdf)
            latexStringBuilder.append("\\subsection{Denklem cozumu}").append("\n");
        for (int i=0; i<e6.length-1; i++) {
            for (int j=i+1; j<e6.length; j++) {
                e6 = fd.gaussMatrix(e6, i, j);
                if (toText) {
                    stringBuilder.append((i + 1)).append(". satir kullanilarak  asagisindaki (").append(j + 1).append(",").append(i + 1).append(") elemani 0 yapilir.").append("\n");
                    stringBuilder.append(fd.showMatrix(e6)).append("\n");
                    stringBuilder.append("------").append("\n");
                }
                if (toPdf) {
                    latexStringBuilder.append((i + 1)).append(". satir kullanilarak  asagisindaki (").append(j + 1).append(",").append(i + 1).append(") elemani 0 yapilir.");
                    latexStringBuilder = PutMatrix(latexStringBuilder, e6, true, "|");
                }
            }
        }

        //Yukariya dogru indirge
        for (int i=e6.length-1; i>0; i--) {
            for (int j=i-1; j>=0; j--) {
                e6 = fd.gaussMatrix(e6, i, j);
                if (toText) {
                    stringBuilder.append((i + 1)).append(". satir kullanilarak  yukarisindaki (").append(j + 1).append(",").append(i + 1).append(") elemani 0 yapilir.").append("\n");
                    stringBuilder.append(fd.showMatrix(e6)).append("\n");
                    stringBuilder.append("------").append("\n");
                }
                if (toPdf) {
                    stringBuilder.append((i + 1)).append(". satir kullanilarak  yukarisindaki (").append(j + 1).append(",").append(i + 1).append(") elemani 0 yapilir.");
                    latexStringBuilder = PutMatrix(latexStringBuilder, e6, true, "|");
                }
            }
        }

        //Birim matrise donustur
        e6 = fd.identityMatrix(e6);
        if (toText) {
            stringBuilder.append("Birim matrise donusum yapilir.").append("\n");
            stringBuilder.append(fd.showMatrix(e6)).append("\n");
            stringBuilder.append("------").append("\n");
        }
        if (toPdf) {
            latexStringBuilder.append("Katsayi matrisi birim matrise donusturulur.").append("\n");
            latexStringBuilder = PutMatrix(latexStringBuilder, e6, true, "|");
        }

        //Katsay� ifadeleri
        //System.out.println("---9---");
        latexStringBuilder.append("Buradan katsayi cozumleri asagidaki gibi belirlenir.\\\\").append("\n");
        for (int i=0; i<e6.length; i++) {
            if (toText)
                stringBuilder.append(fd.showMatrix(e6)).append("\n");
            if (toPdf) {
                String str = e6[i][e6.length].toLatex(true);
                if (i>0)
                    str = "\\frac{1}{h"+(i>1?"^{"+i+"}":"")+"}("+str+")";
                latexStringBuilder.append("$\\displaystyle c_{").append(i).append("}=").append(str).append("$\\\\").append("\n");
            }
        }

        //Polinom integrali
        //System.out.println("---10---");
        latexStringBuilder.append("\\subsection{Alan Hesabi}").append("\n");
        latexStringBuilder.append("$P_{").append(e.length - 1).append("}(x)$ polinomunun integrali alinir.\\\\").append("\n");

        String str = "c_{0}";
        String str2 = "c_{0}x";
        for (int i=1; i<e.length; i++) {
            if (i==4) break;
            str += i==1 ? "+c_{1}x" : "+c_{"+i+"}x^{"+i+"}";
            str2 += "+c_{"+i+"}\\frac{x^{"+(i+1)+"}}{"+(i+1)+"}";
        }
        if (e.length>4) {
            str += (e.length>5?"+ \\cdot\\cdot\\cdot ":"") + "+c_{"+(e.length-1)+"}x^{"+(e.length-1)+"}";
            str2 += (e.length>5?"+ \\cdot\\cdot\\cdot ":"") + "+c_{"+(e.length-1)+"}\\frac{x^{"+e.length+"}}{"+e.length+"}";
        }

        latexStringBuilder.append("\\[ I=\\int_{0}^{").append(e.length - 1).append("h} P_{").append(e.length - 1).append("}(x) \\,dx \\]").append("\n");
        latexStringBuilder.append("\\[ I=\\int_{0}^{").append(e.length - 1).append("h} (").append(str).append(") \\,dx \\]").append("\n");
        latexStringBuilder.append("\\[ I=(").append(str2).append(")\\bigg\\vert_{0}^{").append(e.length - 1).append("h} \\]").append("\n");

        latexStringBuilder.append("Burada $x$ yerine $").append(e.length - 1).append("h$ yerlestirildiginde polinom ifadesinde bulunan $c_{k}*x^{k}$ terimlerinin tamam h ortak parantezine alinabilmektedir.\\\\").append("\n");

        Table tb = new Table();
        tb.put("x", new Id((e.length-1)+"h"));

        Exp p1 = fd.polynomial_integ();
        Texp pe = new Texp(new Id("I"), p1.insert(tb));
        if (toText)
            stringBuilder.append(pe).append("\n");
        if (toPdf)
            latexStringBuilder.append("$\\displaystyle ").append(pe.toLatex(true)).append("$\\\\\\\\").append("\n");

        tb.put("x", new Ratio(e.length-1));
        for (int i=0; i<e6.length; i++) {
            tb.put("c"+i, e6[i][e6.length]);
        }

        Texp pe2 = new Texp(new Id("I"), new Times (new Id("h"), new Par(p1.insert(tb))));
        //System.out.println("pe2: " + pe2.toClass());
        latexStringBuilder.append("$\\displaystyle ").append(pe2.toLatex(true)).append("$\\\\\\\\").append("\n");
        Texp pe3 = new Texp(new Id("I"), new Times(new Id("h"), new Par(pe2.getE2().getE2().reduce())));
        stringBuilder.append("pe3: ").append(pe3.toClass()).append("\n");
        latexStringBuilder.append("$\\displaystyle ").append(pe3.toLatex(true)).append("$\\\\\\\\").append("\n");

        if (pe3.getE2().getE2().getE2() instanceof Poly) {
            Poly p = (Poly)pe3.getE2().getE2().getE2();
            if (p.ps[0].getDecimal().compareTo(BigDecimal.ZERO)!=0 && p.ps[0].getDecimal().compareTo(BigDecimal.ONE)!=0) {
                Ratio n = p.ps[0];
                p = p.divideP(n);
                pe3 = new Texp(new Id("I"), new Times(new Times(n, new Id("h")), new Par(p)));
                latexStringBuilder.append("$\\displaystyle "+pe3.toLatex(true)+"$\\\\\\\\").append("\n");
            }
        }

        latexStringBuilder.append("h ve y degerleri ile integral hesaplanir.\\\\").append("\n");

        Texp pe4 = new Texp(new Id("I"), new Times(pe3.getE2().getE1(), pe3.getE2().getE2()).insert(table));
        stringBuilder.append("pe4: ").append(pe4.toClass()).append("\n");
        latexStringBuilder.append("$\\displaystyle ").append(pe4.toLatex(true)).append("$\\\\\\\\").append("\n");

        Texp pe5 = new Texp(new Id("I"), new Ratio(pe4.getE2().eval(table).getDecimal()));
        latexStringBuilder.append("$\\displaystyle ").append(pe5.toLatex(true)).append("$\\\\\\\\").append("\n");

        return new ResponseDTO(stringBuilder, latexStringBuilder, null, null);
    }

    public void Tex_Create() {
        try {
            File file = new File(latexFile+".pdf");
            file.delete();
            pw = new PrintWriter(latexFile+".tex", "UTF-8");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        pw.println("\\documentclass{article}");
        pw.println("\\usepackage{amsmath}");
        pw.println("\\usepackage{amssymb}");
        pw.println("\\usepackage{cancel}");
        pw.println("\\usepackage{setspace}");
        pw.println("\\usepackage{graphicx}");
        pw.println("\\usepackage{enumitem}");
        pw.println("\\usepackage[colorlinks=true, allcolors=blue]{hyperref}");
        pw.println("\\usepackage[english]{babel}");
        pw.println("\\usepackage[letterpaper,top=2cm,bottom=2cm,left=1cm,right=2cm,marginparwidth=1 cm]{geometry}");
        pw.println("\\usepackage{nicefrac, xfrac}");
        pw.println("\\usepackage{indentfirst}");
        pw.println("\\onehalfspacing");
        pw.println("\\begin{document}");
        pw.println("\\setlength\\parindent{0pt}"); //Set noindent for entire file
    }

    public void Tex_Close(){
        pw.println("\\end{document}");
        pw.close();
    }

    public void PutText(String s){
        pw.print(s);
    }

    //public void PutTextLn(String s){
    //    pw.println(s);
    //}

    public void PutTextLnLn(String s){
        pw.println(s + "\n\n");
    }

    public StringBuilder PutMatrix(StringBuilder sb, Exp[][] ms, boolean ltx, String or) {
        String header = "";
        for(int i=0;i<ms[0].length-1;i++){
            header += "r";
        }
        header += or + "r";

        sb.append("\\begin{center}").append("\n");
        sb.append("$$ \\left[\\begin{array}{").append(header).append("}").append("\n");

        sb.append("c_{0}");
        for(int i=1;i<ms[0].length-1;i++){
            sb.append(" & c_{").append(i).append("}");
        }
        sb.append("\\\\").append("\n");

        for (int i=0; i<ms.length; i++) {
            sb.append(ms[i][0].toLatex(false)+"");
            for (int j=1; j<ms[i].length; j++) {
                sb.append(" & ").append(ms[i][j].toLatex(ltx));
            }
            sb.append("\\\\").append("\n");
        }

        sb.append("\\end{array}\\right] $$").append("\n");
        sb.append("\\end{center}").append("\n");
        return sb;
    }

    public ResponseDTO Tex_pdf(ResponseDTO responseDTO) {
        try {
            ProcessBuilder builder = new ProcessBuilder("pdflateX", latexFile+".tex");
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            var file = new File(latexFile+".pdf");
            if (file.exists()) {
                //Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+latexFile+".pdf");
                //p.waitFor();
                responseDTO.setPdfFile(Files.readAllBytes(file.toPath()));
                responseDTO.setPdfFilePath(file.getAbsolutePath());
                return responseDTO;
            }
            else {
                System.out.println("File does not exists: "+latexFile+".pdf");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
