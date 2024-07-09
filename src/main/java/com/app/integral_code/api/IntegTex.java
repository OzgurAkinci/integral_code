package com.app.integral_code.api;

import java.io.*;
import java.math.BigDecimal;

public class IntegTex {
  public static PrintWriter pw = null;
  public static String latexFile = "PInteg";
  
  public static void main(String[] args) {
    //int[] m = {0,1,2,3,4};
    int[] m = {0,1,2,3,4,5,6,7,8};
  	//int[] m = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
  	//int[] m = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30};
  	//int[] m = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40};
    /*int[] m = new int[50];
    for (int i=0; i<50; i++) {
      m[i] = i;
    }*/
    
    Table table = new Table();
    
    //h ve y de�erlerini tabloya yaz.
    //int h = 1;
    double h = Math.PI/20;
    double y0 = Math.PI/5;
    table.put("h", new Ratio(h));
    for (int i=0; i<m.length; i++) {
      //table.put("y"+i, new Ratio(y0+i*h));
      table.put("y"+i, new Ratio(Math.sin(y0+i*h)));
    }

    boolean toText = true;  //Metin bazli goster
    boolean toPdf = true;   //Pdf olustur
    new IntegTex().FindInteg(table, m, toText, toPdf);
  }

  public void FindInteg(Table table, int[] m, boolean toText, boolean toPdf) {
    PInteg fd = new PInteg(m);

  	Tex_Create();
  	
  	if (toText)
  	  System.out.println("Sayisal Integral icin Polinom Yaklasim Form�lleri");
  	if (toPdf)
	  PutTextLn("\\section{Say�sal �ntegral i�in Polinom Yakla��m Form�lleri}");
	
	//System.out.println("---1---");
	if (toText) {
	  System.out.println("Polinom Ifadesi");
	  System.out.println("Bir Pn(x) polinomu genel olarak asagidaki ifade ile temsil edilir.");
	  System.out.println("Pn(x) = c0+c1*x+c2*x^2+c3*x^3+...+cn*x^n");
	}
	if (toPdf) {
	  PutTextLn("\\subsection{Polinom �fadesi}");
	  PutTextLn("Bir $P_{n}(x)$ polinomu genel olarak a�a��daki ifade ile temsil edilir.\\\\");
	  PutTextLn("$\\displaystyle P_{n}(x)= c_{0}+c_{1}x+c_{2}x^{2}+c_{3}x^{3}+\\cdot\\cdot\\cdot+c_{n}x^{n}$");
	}
	
	//System.out.println("---2---");
	if (toText) {
	  System.out.println("Ornek Noktalar");	
	  System.out.println("Bu ifade xm=x0+mh biciminde secilen her bir nokta icin duzenlenir.");
	}
	if (toPdf) {
	  PutTextLn("\\subsection{�rnek Noktalar}");
	  PutTextLn("Bu ifade $x_{m}=x_{0}+mh$ bi�iminde se�ilen her bir nokta i�in d�zenlenir.\\\\");
	}
    Texp[] e = fd.polynomial();
    for (int i=0; i<e.length; i++) {
      if (toText)
        System.out.println(e[i]);
      if (toPdf)
        PutTextLn("$\\displaystyle P_{"+(e.length-1)+"}("+fd.mh[i]+"*h)="+e[i].toLatex(false)+"$\\\\");
    }
    
    //System.out.println("---6---");
	//Exp[][] e5 = fd.coeffMatrix(e4);  //(1)
	System.out.println("Class: " + e[2].toClass());
	if (toText) {
	  System.out.println("Denklem Sistemi");
	  System.out.println("Bu denklemlerden katsay� matrisi olusturulur (h, h^{2} gibi terimler daha sonra ��z�me eklenecektir).");
	}
	if (toPdf) {
	  PutTextLn("\\subsection{Denklem Sistemi}");
	  PutTextLn("Bu denklemlerden katsay� matrisi olu�turulur.");
	}
	Exp[][] e5 = fd.coeffMatrix(e); //(2)
	if (toText)
	  System.out.println(fd.showMatrix(e5));
	if (toPdf)
      //PutMatrix(e5, false, "");
      PutMatrix(e5, false, "|");
    
    //System.out.println("---7---");
    if (toText)
      System.out.println("Denklemler, uslu ifadeler hesaplanarak a�agidaki gibi yeniden duzenlenir.");
    if (toPdf)
	  PutTextLn("Denklemler, �sl� ifadeler hesaplanarak a�a��daki gibi yeniden d�zenlenir.");
    Exp[][] e6 = fd.integMatrix(e5);      //(3)
    //Exp[][] e6 = fd.integMatrix(e5, d); //(4)
    if (toText)
      System.out.println(fd.showMatrix(e6));
    if (toPdf)
      PutMatrix(e6, false, "|");
      
    //System.out.println("---8---");    
    //Asagiya dogru indirge
    if (toText)
      System.out.println("Denklem Cozumu");
    if (toPdf)
      PutTextLn("\\subsection{Denklem ��z�m�}");
    for (int i=0; i<e6.length-1; i++) {
      for (int j=i+1; j<e6.length; j++) {
        e6 = fd.gaussMatrix(e6, i, j);
        if (toText) {
          System.out.println((i+1)+". sat�r kullan�larak  a�a��s�ndaki ("+(j+1)+","+(i+1)+") elemani 0 yap�l�r.");
          System.out.println(fd.showMatrix(e6));
          System.out.println("------");
        }
        if (toPdf) {
          PutText((i+1)+". sat�r kullan�larak  a�a��s�ndaki ("+(j+1)+","+(i+1)+") eleman� 0 yap�l�r.");
          PutMatrix(e6, true, "|");
        }
      }
    }
    
    //Yukariya dogru indirge
    for (int i=e6.length-1; i>0; i--) {
      for (int j=i-1; j>=0; j--) {
        e6 = fd.gaussMatrix(e6, i, j);
        if (toText) {
          System.out.println((i+1)+". sat�r kullan�larak  yukar�s�ndaki ("+(j+1)+","+(i+1)+") elemani 0 yap�l�r.");
          System.out.println(fd.showMatrix(e6));
          System.out.println("------");
        }
        if (toPdf) {
          PutText((i+1)+". sat�r kullan�larak  yukar�s�ndaki ("+(j+1)+","+(i+1)+") eleman� 0 yap�l�r.");
          PutMatrix(e6, true, "|");
        }
      }
    }
    
    //Birim matrise donustur
    e6 = fd.identityMatrix(e6);
    if (toText) {
      System.out.println("Birim matrise d�n���m yap�l�r.");
      System.out.println(fd.showMatrix(e6));
      System.out.println("------");      
    }
    if (toPdf) {
      PutTextLn("Katsay� matrisi birim matrise d�n��t�r�l�r.");
      PutMatrix(e6, true, "|");
    }
    
    //Katsay� ifadeleri
    //System.out.println("---9---");
	PutTextLn("Buradan katsay� ��z�mleri a�a��daki gibi belirlenir.\\\\");
    for (int i=0; i<e6.length; i++) {
      if (toText)
        System.out.println(fd.showMatrix(e6));
      if (toPdf) {
      	String str = e6[i][e6.length].toLatex(true);
      	if (i>0)
      	  str = "\\frac{1}{h"+(i>1?"^{"+i+"}":"")+"}("+str+")";
        PutTextLn("$\\displaystyle c_{"+i+"}="+str+"$\\\\");
      }
    }
    
    //Polinom integrali
    //System.out.println("---10---");
    PutTextLn("\\subsection{Alan Hesab�}");
    PutTextLn("$P_{"+(e.length-1)+"}(x)$ polinomunun integrali al�n�r.\\\\");
    
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

    PutTextLn("\\[ I=\\int_{0}^{"+(e.length-1)+"h} P_{"+(e.length-1)+"}(x) \\,dx \\]");
    PutTextLn("\\[ I=\\int_{0}^{"+(e.length-1)+"h} ("+str+") \\,dx \\]");
    PutTextLn("\\[ I=("+str2+")\\bigg\\vert_{0}^{"+(e.length-1)+"h} \\]");
    
    PutTextLn("Burada $x$ yerine $"+(e.length-1)+"h$ yerle�tirildi�inde polinom ifadesinde bulunan $c_{k}*x^{k}$ terimlerinin tamam� h ortak parantezine al�nabilmektedir.\\\\");
    
    Table tb = new Table();
    tb.put("x", new Id((e.length-1)+"h"));

    Exp p1 = fd.polynomial_integ();
    Texp pe = new Texp(new Id("I"), p1.insert(tb));
    if (toText)
      System.out.println(pe);
    if (toPdf)
      PutTextLn("$\\displaystyle "+pe.toLatex(true)+"$\\\\\\\\");

    tb.put("x", new Ratio(e.length-1));
    for (int i=0; i<e6.length; i++) {
      tb.put("c"+i, e6[i][e6.length]);
    }
    
    Texp pe2 = new Texp(new Id("I"), new Times (new Id("h"), new Par(p1.insert(tb))));
    //System.out.println("pe2: " + pe2.toClass());
    PutTextLn("$\\displaystyle "+pe2.toLatex(true)+"$\\\\\\\\");
    Texp pe3 = new Texp(new Id("I"), new Times(new Id("h"), new Par(pe2.getE2().getE2().reduce())));
    System.out.println("pe3: " + pe3.toClass());
    PutTextLn("$\\displaystyle "+pe3.toLatex(true)+"$\\\\\\\\");
    
    if (pe3.getE2().getE2().getE2() instanceof Poly) {
      Poly p = (Poly)pe3.getE2().getE2().getE2();
      if (p.ps[0].getDecimal().compareTo(BigDecimal.ZERO)!=0 && p.ps[0].getDecimal().compareTo(BigDecimal.ONE)!=0) {
      	Ratio n = p.ps[0];
        p = p.divideP(n);
        pe3 = new Texp(new Id("I"), new Times(new Times(n, new Id("h")), new Par(p)));
        PutTextLn("$\\displaystyle "+pe3.toLatex(true)+"$\\\\\\\\");
      }
    }
    
    PutTextLn("h ve y de�erleri ile integral hesaplan�r.\\\\");
    
    Texp pe4 = new Texp(new Id("I"), new Times(pe3.getE2().getE1(), pe3.getE2().getE2()).insert(table));
    System.out.println("pe4: " + pe4.toClass());
    PutTextLn("$\\displaystyle "+pe4.toLatex(true)+"$\\\\\\\\");
    
    Texp pe5 = new Texp(new Id("I"), new Ratio(pe4.getE2().eval(table).getDecimal()));
    PutTextLn("$\\displaystyle "+pe5.toLatex(true)+"$\\\\\\\\");

    Tex_Close();
    if (toPdf)
      Tex_pdf(toPdf);
  }
  
  public void PutMatrix(Exp[][] ms, boolean ltx, String or) {
    String header = "";
	for(int i=0;i<ms[0].length-1;i++){
	  header += "r";
	}
	header += or + "r";
	
	PutTextLn("\\begin{center}");
  	PutTextLn("$$ \\left[\\begin{array}{"+header+"}");
  	
  	PutText("c_{0}");
  	for(int i=1;i<ms[0].length-1;i++){
	  PutText(" & c_{"+i+"}");
	}
	PutTextLn("\\\\");
	
    for (int i=0; i<ms.length; i++) {
      PutText(ms[i][0].toLatex(false)+"");
      for (int j=1; j<ms[i].length; j++) {
        PutText(" & " + ms[i][j].toLatex(ltx));
      }
      PutTextLn("\\\\");
    }
    
    PutTextLn("\\end{array}\\right] $$");
  	PutTextLn("\\end{center}");
  }

  public void Tex_pdf(boolean toPdf) {
	try {
	  ProcessBuilder builder = new ProcessBuilder("pdflateX", latexFile+".tex");
	  builder.redirectErrorStream(true);
	  Process process = builder.start();
	  InputStream is = process.getInputStream();
	  InputStreamReader isr = new InputStreamReader(is);
	  BufferedReader br = new BufferedReader(isr);
	  String line;
	  while ((line = br.readLine()) != null) {
	  	if (toPdf)
	      System.out.println(line);
	  }
	} catch (IOException ex) {
	  ex.printStackTrace();
	}
	
	try {
	  if ((new File(latexFile+".pdf")).exists()) {
		Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+latexFile+".pdf");
	    p.waitFor();
	  }
	  else {
	    System.out.println("File does not exists: "+latexFile+".pdf");
	  }
  	} catch (Exception ex) {
	  ex.printStackTrace();
	}
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

  public void PutTextLn(String s){
	pw.println(s);
  }

  public void PutTextLnLn(String s){
	pw.println(s + "\n\n");
  }
}

