package com.app.integral_code.api;

import java.math.BigInteger;

public class PInteg {
  public int[] mh;
  /*public static char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
  public static String[][] turkish_letters =
  	{{"a", "\\^{a}"}, {"A", "\\^{A}"}, {"c", "\\c{c}"}, {"C", "\\c{C}"}, {"g", "\\u{g}"}, {"G", "\\u{G}"}, {"i", "{\\i}"},
  	{"I", "\\.{I}"}, {"o", "\\\"{o}"}, {"O", "\\\"{O}"}, {"s", "\\c{s}"}, {"S", "\\c{S}"}, {"u", "\\\"{u}"}, {"U", "\\\"{U}"}};

  public static String getTL(String c) {
    for (String[] s : turkish_letters)
      if (s[0].equals(c))
      	return s[1];
    return "#";
  }*/
  
  public PInteg(int[] a) {
    mh = a;
  }
  
  public int[] getM() {
    return mh;
  }
  
  public String showMatrix(Exp[][] ms) {
    String str = "";
    for (int i=0; i<ms.length; i++) {
      for (int j=0; j<ms[i].length; j++) {
        str += ms[i][j] + "  ";
      }
      str += "\n";
    }
    return str;
  }

  //Right-associative sum
  public Texp[] polynomial() {
  	int s = mh.length;
    Texp[] mx = new Texp[s];
    for (int i=s-1; i>=0; i--) {
      Exp e = new Times(new Ratio(new Power(new Num(mh[i]), new Num(s-1))), new Times(new Id("c"+(s-1)),new Power(new Id("h"),new Num(s-1))));
      for (int j=s-2; j>=0; j--) {
      	e = new Plus(new Times(new Ratio(new Power(new Num(mh[i]), new Num(j))), new Times(new Id("c"+j),new Power(new Id("h"),new Num(j)))), e);
      }
      mx[i] = new Texp(new Id("y"+mh[i]), e);
    }
    return mx;
  }

  //Right-associative sum
  public Exp polynomial_integ() {
  	int s = mh.length;
    Exp e = new Times(new Id("c"+(s-1)),new Ratio(new Power(new Id("x"),new Ratio(s)),new Ratio(s)));
    for (int j=s-2; j>=0; j--) {
      e = new Plus(new Times(new Id("c"+j),new Ratio(new Power(new Id("x"),new Ratio(j+1)),new Ratio(j+1))), e);
    }
    return e;
  }
  
  public Texp[] multiply(Texp[] e) {
    int s = e.length;
    Texp[] es = new Texp[s];
    Exp ev;
    for (int i=0; i<s; i++) {
      ev = new Id("t"+(i+1));
      //es[i] = mulbyId(e[i], new Id(alphabet[i]+""));
      es[i] = new Texp(new Times(ev, e[i].getE1()), mulbyId(e[i].getE2(), ev));
    }
    return es;
  }
  
  public Exp mulbyId(Exp e, Exp t) {
    if (e instanceof Plus)
      return new Plus(new Times(new Times(t, e.getE1().getE1()), e.getE1().getE2()), mulbyId(e.getE2(), t));
    else if (e instanceof Times)
      return new Times(new Times(t, e.getE1()), e.getE2());
    return e;
  }
  
  public Texp add(Texp[] e) {
    int s = e.length;
    Exp[] es = new Exp[s];
    es[s-1] = e[s-1].getE2();
  	Exp et = e[s-1].getE1();
  	for (int i=s-2; i>=0; i--) {
  	  es[i] = e[i].getE2();
  	  et = new Plus(e[i].getE1(), et);
  	}
  	return new Texp(et, addTerms(es));
  }
  	
  public Exp addTerms(Exp[] e) {
    Exp et;
  	int s = e.length;
  	if (e[0] instanceof Plus) {
  	  Exp[] es = new Exp[s];
  	  es[s-1] = e[s-1].getE2();
  	  et = e[s-1].getE1().getE1();
  	  for (int i=s-2; i>=0; i--) {
  	    es[i] = e[i].getE2();
  	    et = new Plus(e[i].getE1().getE1(), et);
  	  }
  	  return new Plus(new Times(new Par(et), e[0].getE1().getE2()), addTerms(es));
  	}
  	else if (e[0] instanceof Times) { //last term
  	  et = e[s-1].getE1();
  	  for (int i=s-2; i>=0; i--) {
  	    et = new Plus(e[i].getE1(), et);
  	  }
  	  return new Times(new Par(et), e[0].getE2());
  	}
  	return null;
  }

  public Exp[] equations(Texp te, int s) {
    Exp[] es = new Exp[s+1];
    Exp e = te.getE2();
    int i = 0;
    while (true) {
      es[i++] = e.getE1().getE1();
      e = e.getE2();
      if (e instanceof Times) {
        es[i] = e.getE1();
        break;
      }
    }
    return es;
  }

  public Exp[][] coeffMatrix(Texp[] es) {
    int s = es.length;
    Exp[][] ns = new Exp[s][s+1];
    for (int i=0; i<s; i++) {
      int j = 0;
      Exp e = es[i].getE2();
      Poly p = new Poly(s);
      p.setP(Integer.parseInt(((Id)es[i].getE1()).id.substring(1)));
      ns[i][s] = p;
      while (true) {
        if (e instanceof Times) {
          ns[i][j++] = e.getE1();
          break;
        }
        ns[i][j++] = e.getE1().getE1();
        e = e.getE2();
      }
    }
    return ns;
  }
  
  public Exp[][] coeffMatrix(Exp[] es) {
    int s = es.length;
    Exp[][] ns = new Exp[s][s-1];
    for (int i=0; i<s; i++) {
      int j = 0;
      Exp e = es[i];
      while (true) {
        e = e.getE2();
        if (e instanceof Times) {
          ns[i][j++] = e.getE2();
          break;
        }
        ns[i][j++] = e.getE1().getE2();
      }
    }
    return ns;
  }
  
  public Exp[][] coeffMatrix(Exp[] es, int d) {
    int s = es.length;
    Exp[][] ns = new Exp[s-2][s-1];
    //ilk satiri ve d. satiri cikar.
    int k = 0;
    for (int i=1; i<s; i++) {
      if (i==d)
      	continue;
      int j = 0;
      Exp e = es[i];
      while (true) {
        e = e.getE2();
        if (e instanceof Times) {
          ns[k][j++] = e.getE2();
          break;
        }
        ns[k][j++] = e.getE1().getE2();
      }
      k++;
    }
    return ns;
  }

  public Exp[][] integMatrix(Exp[][] es) {
    int s = es.length;
    Exp[][] ns = new Exp[s][s+1];
    for (int i=0; i<s; i++) {
      for (int j=0; j<s; j++) {
        //ns[i][j] = es[i][j];
        //ns[i][j] = new Ratio(es[i][j].getDecimal());
        ns[i][j] = es[i][j].getE1();
        ns[i][s] = es[i][s];
      }
    }
    return ns;
  }
  
  public Exp[][] integMatrix(Exp[][] es, int d) {
    int s = es.length;
    Exp[][] ns = new Exp[s-2][s-1];
    int k = 0;
    for (int i=1; i<s; i++) {
      if (i != d) {
        for (int j=0; j<s-1; j++) {
    	  //ns[k][j] = es[i][j];
    	  ns[k][j] = es[i][j].getE1();
    	  if (j == s-2)
    	    ns[k][j] = ((Ratio)ns[k][j]).multiplyR(new Ratio(-1));
        }
        k++;
      }
    }
    return ns;
  }
  
  public Exp[][] gaussMatrix(Exp[][] ns, int r, int r2) {
    int s = ns.length;
    //r. satiri kullanarak r2. satiri indirge
    Ratio factor = ((Ratio)ns[r2][r]).divideR((Ratio)ns[r][r]);
    for (int j=r; j<s; j++) {
      ns[r2][j] = ((Ratio)ns[r2][j]).subtractR(factor.multiplyR((Ratio)ns[r][j])).Simplify();
    }
    //ns[r2][s] = ((Poly)ns[r2][s]).reduceP2((Poly)ns[r2][s], factor, (Poly)ns[r][s]);
    ((Poly)ns[r2][s]).reduceP(factor, (Poly)ns[r][s]);
    return ns;
  }

  public Exp[][] identityMatrix(Exp[][] ns) {
    int s = ns.length;
    //Kosegen elemanlarini 1 yap
    for (int i=0; i<s; i++) {
      //ns[i][s] = ((Ratio)ns[i][s]).divideR((Ratio)ns[i][i]).Simplify();
      ((Poly)ns[i][s]).divideP((Ratio)ns[i][i]);
      ns[i][i] = new Ratio(1);
    }
    return ns;
  }

/////////////////////////
  public Exp[][] coeffVarMatrix(Exp[] es) {
    int s = es.length;
    Exp[][] ns = new Exp[s][s-1];
    for (int i=0; i<s; i++) {
      int j = 0;
      Exp e = es[i];
      while (true) {
        e = e.getE2();
        if (e instanceof Times) {
          ns[i][j++] = e;
          break;
        }
        ns[i][j++] = e.getE1();
      }
    }
    return ns;
  }
  
  public BigInteger fact(int n) {
	BigInteger result = BigInteger.ONE;
    for(int i = 2; i <= n; i++)
      result = result.multiply(BigInteger.valueOf(i));
	return result;
  }
  
  public BigInteger pow(int taban, int us){
	BigInteger sonuc = BigInteger.ONE;
	for (int i = 0; i < us; i++) {
	  sonuc = sonuc.multiply(BigInteger.ONE.valueOf(taban));
	}
	return sonuc;
  }
  
  public Ratio[][] horMx() {
  	int s = mh.length;
    Ratio[][] mx = new Ratio[s][s+1];
    for (int i=0; i<s; i++) {
      for (int j=0; j<s+1; j++) {
        mx[i][j] = new Ratio(new Power(new Num(mh[i]), new Num(j)), new RFact(j));
      }
    }
    return mx;
  }
  
  public Ratio[][] verMx() {
  	int s = mh.length;
    Ratio[][] mx = new Ratio[s+1][s];
    for (int i=0; i<s+1; i++) {
      for (int j=0; j<s; j++) {
        mx[i][j] = new Ratio(new Power(new Num(mh[j]), new Num(i)), new RFact(i));
      }
    }
    return mx;
  }

  public Exp[] verTMx() {
  	int s = mh.length;
    Exp[] mx = new Exp[s+1];
    for (int i=s; i>=0; i--) {
      Exp e = new Times(new Ratio(new Power(new Num(mh[s-1]), new Num(i)), new RFact(i)),new Times(new Power(new Id("h"),new Num(i)),new Diff(i)));
      for (int j=s-2; j>=0; j--) {
      	e = new Plus(new Times(new Ratio(new Power(new Num(mh[j]), new Num(i)), new RFact(i)),new Times(new Power(new Id("h"),new Num(i)),new Diff(i))), e);
      }
      mx[i] = e;
    }
    return mx;
  }	
}
