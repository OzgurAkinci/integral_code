package com.app.integral_code.api;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

class Texp {
  Exp e1, e2;
  public Texp(Exp a, Exp b) {
    e1 = a;
    e2 = b;
  }
  
  public Exp getE1() {
    return e1;
  }

  public Exp getE2() {
    return e2;
  }

  public String toString() {
    return e1 + "=" + e2;
  }
  
  public String toLatex(boolean x) {
    return e1.toLatex(x) + "=" + e2.toLatex(x);
  }
  
  public String toClass() {
    return "Texp("+e1.toClass()+","+e2.toClass()+")";
  }
}

abstract class Exp {
  public abstract Exp getE1();
  public abstract Exp getE2();
  public abstract String toClass();
  public abstract Exp insert(Table t);
  public abstract Exp reduce();
  public Ratio eval(Table t) { return new Ratio(); }
  public BigDecimal getDecimal() { return BigDecimal.ZERO; }
  public String toLatex(boolean x) { return ""; }
}

class Poly extends Exp {
  Ratio[] ps;
  int d;
  public Poly(int s) {
  	d = s;
    ps = new Ratio[s];
    for (int i=0; i<d; i++)
      ps[i] = new Ratio(0);
  }
  public void setP(int r) {
    ps[r] = new Ratio(1);
  }
  public Poly addP(Poly p) {
    for (int i=0; i<d; i++)
      ps[i] = ps[i].addR(p.ps[i]).Simplify();
    return this;
  }
  public Poly multiplyP(Ratio n) {
    for (int i=0; i<ps.length; i++)
      ps[i] = n.multiplyR(ps[i]).Simplify();
    return this;
  }
  public Poly divideP(Ratio n) {
    for (int i=0; i<ps.length; i++) {
      ps[i] = ps[i].divideR(n).Simplify();
    }
    return this;
  }
  public void reduceP(Ratio n, Poly p) {
    for (int i=0; i<d; i++) {
      ps[i] = (ps[i].subtractR(p.ps[i].multiplyR(n))).Simplify();
    }
  }
  public Poly reduceP2(Poly p1, Ratio n, Poly p2) {
    Poly  p = new Poly(p1.d);
    for (int i=0; i<d; i++)
      p.ps[i] = p1.ps[i].subtractR(n.multiplyR(p2.ps[i])).Simplify();
    return p;
  }
  public Poly addP(Poly p1, Poly p2) {
  	Poly  p = new Poly(p1.d);
    for (int i=0; i<p.d; i++)
      p.ps[i]=p1.ps[i].addR(p2.ps[i]);
    return p;
  }
  public Exp getE1() {
    return this;
  }
  public Exp getE2() {
    return this;
  }
  public Exp insert(Table t) {
    Exp e = new Times(ps[0], new Id("y"+0));
    for (int i=1; i<ps.length; i++) {
  	  if (ps[i].toString().equals("0"))
  	    continue;
  	  if (ps[i].toString().equals("1"))
  	    e = new Plus(e, new Id("y"+i));
  	  else if (ps[i].toString().equals("-1"))
  	    e = new Minus(e, new Id("y"+i));
  	  else if (ps[i].toString().charAt(0)=='-')
  	    e = new Minus(e, new Times(ps[i].abs(), new Id("y"+i)));
  	  else
  	    e = new Plus(e, new Times(ps[i], new Id("y"+i)));
    }
    return e.insert(t);
  }
  public Exp reduce() {
    return this;
  }
  public Ratio eval(Table t) {
    BigDecimal big = BigDecimal.ZERO;
    for (int i=0; i<ps.length; i++) {
      if (ps[i]!= null)
        big = big.add(ps[i].eval(t).getDecimal());
    }
    return new Ratio(big);
  }
  public String toString() {
  	String str = "";
  	for (int i=0; i<ps.length; i++) {
  	  if (ps[i].toString().equals("0"))
  	    continue;
  	  if (ps[i].toString().equals("1"))
  	  	str += "+y"+i;
  	  else if (ps[i].toString().equals("-1"))
  	  	str += "-y"+i;
  	  else if (ps[i].toString().charAt(0)=='-')
  	  	str += "-"+ps[i].abs()+"y"+i;
  	  else
  	  	str += "+"+ps[i]+"y"+i;
  	}
    return "["+(str.length()>0 ? (str.charAt(0)=='+'?str.substring(1):str) : "")+"]";
  }  
  public String toLatex(boolean x) {
    String str = "";
    for (int i=0; i<ps.length; i++) {
  	  if (ps[i].toString().equals("0"))
  	    continue;
  	  if (ps[i].toString().equals("1"))
  	  	str += "+y_{"+i+"}";
  	  else if (ps[i].toString().equals("-1"))
  	  	str += "-y_{"+i+"}";
  	  else if (ps[i].toString().charAt(0)=='-')
  	  	str += "-"+ps[i].abs().toLatex(x)+"y_{"+i+"}";
  	  else
  	  	str += "+"+ps[i].toLatex(x)+"y_{"+i+"}";
    }
    return str.length()>0 ? (str.charAt(0)=='+'?str.substring(1):str) : "";
  }
  public String toClass() {
    String str = "";
    for (int i=0; i<ps.length; i++) {
  	  if (ps[i].getDecimal().compareTo(BigDecimal.ZERO)!=0)
        str += "+(" + ps[i].toClass() + "," + i + ")";
    }
    return "Poly("+(str.length()>0?str.substring(1):"")+")";
  }
}

class Plus extends Exp {
  Exp e1, e2;
  public Plus(Exp a, Exp b) {
    e1 = a;
    e2 = b;
  }
  
  public Exp getE1() {
    return e1;
  }

  public Exp getE2() {
    return e2;
  }

  public Exp insert(Table t) {
    //return new Plus(e1.insert(t), e2.insert(t));
  	Exp a = e1.insert(t);
  	Exp b = e2.insert(t);
  	if ((a instanceof Ratio && ((Ratio)a).getDecimal().signum()==0) && (b instanceof Ratio && ((Ratio)b).getDecimal().signum()==0))
  	  return new Ratio();
  	if (a instanceof Ratio && ((Ratio)a).getDecimal().signum()==0)
  	  return b;
  	if (b instanceof Ratio && ((Ratio)b).getDecimal().signum()==0)
  	  return a;
    return new Plus(a, b);
  }
  
  public Exp reduce() {
    Exp e1 = this.e1.reduce();
    Exp e2 = this.e2.reduce();
    if (e1 instanceof Poly && e2 instanceof Poly)
      return ((Poly)e1).addP((Poly)e2);
    return new Plus(e1, e2);
  }

  public Ratio eval(Table t) {
    return new Ratio(e1.eval(t)).addR(e2.eval(t));
  }

  public String toString() {
    return e1 + "+" + e2;
  }
  
  public String toLatex(boolean x) {
    return e1.toLatex(x) + "+" + e2.toLatex(x);
  }
  
  public String toClass() {
    return "Plus("+e1.toClass()+","+e2.toClass()+")";
  }
}

class Minus extends Exp {
  Exp e1, e2;
  public Minus(Exp a, Exp b) {
    e1 = a;
    e2 = b;
  }
  
  public Exp getE1() {
    return e1;
  }

  public Exp getE2() {
    return e2;
  }

  public Exp insert(Table t) {
    return new Minus(e1.insert(t), e2.insert(t));
  }
  
  public Exp reduce() {
    Exp e1 = this.e1.reduce();
    Exp e2 = this.e2.reduce();
    if (e1 instanceof Poly && e2 instanceof Poly)
      return ((Poly)e1).addP(((Poly)e2).multiplyP(new Ratio(-1)));
    return new Minus(e1, e2);
  }

  public Ratio eval(Table t) {
    return new Ratio(e1.eval(t)).subtractR(e2.eval(t));
  }

  public String toString() {
    return e1 + "-" + e2;
  }

  public String toLatex(boolean x) {
    return e1.toLatex(x) + "-" + e2.toLatex(x);
  }

  public String toClass() {
    return "Minus("+e1.toClass()+","+e2.toClass()+")";
  }
}

class Times extends Exp {
  int n;
  Exp e1, e2;
  public Times(Exp a) {
    n = 0;
    e1 = new Num(1);
    e2 = a;
  }
  public Times(int a, Exp b) {
    n = a;
    e1 = new Num(1);
    e2 = b;
  }
  public Times(Exp a, Exp b) {
    n = 1;
    e1 = a;
    e2 = b;
  }
  public Times(int a, Exp b, Exp c) {
    n = a;
    e1 = b;
    e2 = c;
  }
  
  public Exp getE1() {
    return e1;
  }

  public Exp getE2() {
    return e2;
  }

  public Exp insert(Table t) {
    //return new Times(e1.insert(t), e2.insert(t));
  	Exp a = e1.insert(t);
  	Exp b = e2.insert(t);
  	if (a instanceof Ratio && ((Ratio)a).getR1().compareTo(BigDecimal.ZERO)==0 && b instanceof Ratio && ((Ratio)b).getR1().compareTo(BigDecimal.ZERO)==0)
  	  return new Times(a, b);
  	//if ((a instanceof Ratio && ((Ratio)a).getDecimal().signum()==0) || (b instanceof Ratio && ((Ratio)b).getDecimal().signum()==0))
  	if ((a instanceof Ratio && ((Ratio)a).getR2().compareTo(BigDecimal.ZERO)==0) || (b instanceof Ratio && ((Ratio)b).getR2().compareTo(BigDecimal.ZERO)==0))
  	  return new Ratio();
    return new Times(a, b);
  }
  
  public Exp reduce() {
  	Exp e1 = this.e1.reduce();
  	Exp e2 = this.e2.reduce();
    if (e1 instanceof Poly && e2 instanceof Ratio)
      return ((Poly)e1).multiplyP((Ratio)e2);
    if (e1 instanceof Ratio && e2 instanceof Poly)
      return ((Poly)e2).multiplyP((Ratio)e1);
    return new Times(e1, e2);
  }

  public Ratio eval(Table t) {
    return new Ratio(e1.eval(t)).multiplyR(e2.eval(t));
  }

  public String toString() {
    return e1 + "*" + e2;
  }
  
  public String toString2() {
    if (n==0)
      return e2+"";
    else if (n==1)
      return "h*f[1]";
    return e1+"*"+e2+"*h^"+n+"f["+n+"]";
  }

  public String toLatex(boolean x) {
    //System.out.println("e1: " + e1.toClass());
    //System.out.println("e2: " + e2.toClass());
  	if (x)
  	  return e1.toLatex(x) + "*" + e2.toLatex(x);
  	
  	if ((e1 instanceof Num && ((Num)e1).n.intValue()==0) || (e2 instanceof Num && ((Num)e2).n.intValue()==0))
  	  return "0";

  	if (e1 instanceof Num && ((Num)e1).n.intValue()==1)
  	  return e2.toLatex(x);

  	if (e2 instanceof Num && ((Num)e2).n.intValue()==1)
  	  return e1.toLatex(x);

    //h^0/0! terimini ��kar
    if (e1 instanceof Power) {
      if (((Power)e1).e1 instanceof Num && ((Num)((Power)e1).e2).n.intValue()==0)
        return e2.toLatex(x);
      if (((Power)e1).e1 instanceof Id && ((Id)((Power)e1).e1).id.equals("h") && ((Num)((Power)e1).e2).n.intValue()==0)
        return e2.toLatex(x);
    }
    if (e2 instanceof Power) {
      if (((Power)e2).e1 instanceof Num && ((Num)((Power)e2).e2).n.intValue()==0)
        return e1.toLatex(x);
      if (((Power)e2).e1 instanceof Id && ((Id)((Power)e2).e1).id.equals("h") && ((Num)((Power)e2).e2).n.intValue()==0)
        return e1.toLatex(x);
    }
    //1^n terimlerini ��kar
    //n^1 terimlerinden "^1" i ��kar
    if (e1 instanceof Ratio) {
      Exp e = ((Ratio)e1).e1;
      if (e instanceof Power && ((Power)e).e1 instanceof Num && ((Num)((Power)e).e1).n.intValue()==1)
        return e2.toLatex(x);
      if (e instanceof Power && ((Power)e).e2 instanceof Num) {
        //n^0 terimlerini ��kar
        if (((Num)((Power)e).e2).n.intValue()==0)
          return e2.toLatex(x);
        //n^1 terimlerinden "^1" i ��kar
        if (((Num)((Power)e).e2).n.intValue()==1)
          return ((Power)e).e1.toLatex(x) + "*" + e2.toLatex(x);
      }
    }
    if (e2 instanceof Ratio) {
      Exp e = ((Ratio)e2).e1;
      //1^n terimlerini ��kar
      if (e instanceof Power && ((Power)e).e1 instanceof Num && ((Num)((Power)e).e1).n.intValue()==1)
        return e1.toLatex(x);
      if (e instanceof Power && ((Power)e).e2 instanceof Num) {
        //n^0 terimlerini ��kar
        if (((Num)((Power)e).e2).n.intValue()==0)
          return e1.toLatex(x);
        //n^1 terimlerinden "^1" i ��kar
        if (((Num)((Power)e).e2).n.intValue()==1)
          return e1.toLatex(x) + "*" + ((Power)e).e1.toLatex(x);
      }
    }
    return e1.toLatex(x) + "*" + e2.toLatex(x);
  }

  public String toClass() {
    return "Times("+e1.toClass()+","+e2.toClass()+")";
  }
}

class Divide extends Exp {
  Exp e1, e2;
  public Divide(Exp a, Exp b) {
    e1 = a;
    e2 = b;
  }
  
  public Exp getE1() {
    return e1;
  }

  public Exp getE2() {
    return e2;
  }

  public Exp insert(Table t) {
    return new Divide(e1.insert(t), e2.insert(t));
  }
  
  public Exp reduce() {
    Exp e1 = this.e1.reduce();
    Exp e2 = this.e2.reduce();
    if (e1 instanceof Ratio && e2 instanceof Ratio)
      return ((Ratio)e1).divideR((Ratio)e2).Simplify();
    if (e1 instanceof Poly && e2 instanceof Ratio)
      return ((Poly)e1).multiplyP(new Ratio(1).divideR((Ratio)e2));
    return new Divide(e1, e2);
  }

  public Ratio eval(Table t) {
    return new Ratio(e1.eval(t)).divideR(e2.eval(t));
  }

  public String toString() {
    return e1 + "/" + e2;
  }

  public String toLatex(boolean x) {
    return "\\frac{"+e1.toLatex(x)+"}{"+e2.toLatex(x)+"}";
  }

  public String toClass() {
    return "Divide("+e1.toClass()+","+e2.toClass()+")";
  }
}

class Power extends Exp {
  Exp e1, e2;
  public Power(Exp a, Exp b) {
    e1 = a;
    e2 = b;
  }
  
  public Exp getE1() {
    return e1;
  }

  public Exp getE2() {
    return e2;
  }

  public Exp insert(Table t) {
    return new Power(e1.insert(t), e2.insert(t));
  }
  
  public Exp reduce() {
    Exp e1 = this.e1.reduce();
    Exp e2 = this.e2.reduce();
    if (e1 instanceof Ratio && e2 instanceof Ratio)
      return ((Ratio)e1).powR((Ratio)e2);
    return new Power(e1, e2);
  }

  public BigDecimal getDecimal() {
	if (e1 instanceof Num && e2 instanceof Num)
	  return e1.getDecimal().pow(e2.getDecimal().toBigInteger().intValue());
	return BigDecimal.ONE;
  }
  
  public Ratio eval(Table t) {
    return e1.eval(t).powR(e2.eval(t));
  }

  public String toString() {
    return e1 + "^" + e2;
  }
  
  public String toLatex(boolean x) {
    if (e1 instanceof Id && ((Id)e1).id.equals("h")) {
      int n = ((Num)e2).n.intValue();
      if (n==0)
        return "1";
      if (n==1)
        return "h";
      if (!x)
//        return "\\frac{h^{"+n+"}}{"+n+"!}";
        return "h^{"+n+"}";
    }
    //if (e1 instanceof Num && ((Num)e1).n.intValue()==1)
    //  return "1";
    if (e2 instanceof Num && ((Num)e2).n.intValue()==0)
      return "1";
    if (e2 instanceof Num && ((Num)e2).n.intValue()==1)
      return e1.toLatex(x);
    if (e2 instanceof Ratio && ((Ratio)e2).getDecimal().compareTo(BigDecimal.ZERO)==0)
      return "1";
    if (e2 instanceof Ratio && ((Ratio)e2).getDecimal().compareTo(BigDecimal.ONE)==0)
      return e1.toLatex(x);
    if (e1 instanceof Id || e1 instanceof Num || e1 instanceof Ratio || e1 instanceof Par)
      return e1.toLatex(x)+"^{"+e2.toLatex(x)+"}";
    return "("+e1.toLatex(x)+")^{"+e2.toLatex(x)+"}";
  }

  public String toClass() {
    return "Power("+e1.toClass()+","+e2.toClass()+")";
  }
}

class Par extends Exp {
  Exp e;
  public Par(Exp a) {
    e = a;
  }
  
  public Exp getE1() {
    return e;
  }

  public Exp getE2() {
    return e;
  }

  public Exp insert(Table t) {
    return new Par(e.insert(t));
  }
  
  public Exp reduce() {
    //return new Par(e.reduce());
    return e.reduce();
  }

  public Ratio eval(Table t) {
    return e.eval(t);
  }
  
  public BigDecimal getDecimal() {
    return e.getDecimal();
  }

  public String toString() {
    return "(" + e + ")";
  }
  
  public String toLatex(boolean x) {
  	if (!x)
  	  return e.toLatex(x);

    return "("+e.toLatex(x)+")";
  }

  public String toClass() {
    return "Par("+e.toClass()+")";
  }
}

class RFact extends Exp {
  int n;
  public RFact(int a) {
    n = a;
  }
  
  public Exp getE1() {
    return this;
  }

  public Exp getE2() {
    return this;
  }

  public Exp insert(Table t) {
    return new RFact(n);
  }
  
  public Exp reduce() {
    return this;
  }
  
  public Ratio eval(Table t) {
    return new Ratio(n);
  }
  
  public BigInteger fact() {
	BigInteger result = BigInteger.ONE;
    for(int i = 2; i <= n; i++)
      result = result.multiply(BigInteger.valueOf(i));
	return result;
  }
  
  public BigDecimal getDecimal() {
    return new BigDecimal(fact());
  }

  public String toString() {
    return n + "!";
  }
  
  public String toLatex(boolean x) {
    return n + "!";
  }

  public String toClass() {
    return "RFact("+n+")";
  }
}

class Id extends Exp {
  String id;
  public Id(String a) {
    id = a;
  }
  
  public Exp getE1() {
    return this;
  }

  public Exp getE2() {
    return this;
  }
  
  public Exp insert(Table t) {
    Object obj = t.get(id);
    if (obj==null)
      return new Id(id);
    if (obj instanceof Ratio)
      return (Exp)obj;
    return new Par((Exp)obj);
  }
  
  public Exp reduce() {
    return this;
  }
  
  public Ratio eval(Table t) {
    return (Ratio)t.get(id);
  }

  public String toString() {
    return id;
  }
  
  public String toLatex(boolean x) {
    if (id.charAt(0)=='c' || id.charAt(0)=='y')
      return id.charAt(0)+"_{"+id.substring(1)+"}";
    return id;
  }

  public String toClass() {
    return "Id("+id+")";
  }
}

class Num extends Exp {
  BigInteger n;	
  public Num(int a){
    n=BigInteger.valueOf(a);
  }
  
  public Num(BigInteger a){
	n = a;
  }

  public Exp getE1() {
    return this;
  }

  public Exp getE2() {
    return this;
  }

  public Exp insert(Table t) {
    return new Num(n);
  }

  public Exp reduce() {
    return this;
  }

  public Ratio eval(Table t) {
    return new Ratio(n);
  }
  
  public BigDecimal getDecimal() {
    return new BigDecimal(n);
  }

  public String toString(){
	return n + "";
  }
  
  public String toLatex(boolean x) {
    return n.signum()<0 ? "("+n+")" : n+"";
  }

  public String toClass() {
    return "Num("+n+")";
  }
}

class Diff extends Exp {
  int d;
  public Diff(int a) {
    d = a;
  }
  public Exp getE1() {
    return this;
  }

  public Exp getE2() {
    return this;
  }
    
  public Exp insert(Table t) {
    return new Diff(d);
  }

  public Exp reduce() {
    return this;
  }

  public String toString(){
	return "f["+d+"]";
  }
  
  public String toLatex(boolean x) {
    if (d==0)
      return "f(x_{0})";
    if (d==1)
      return "f'(x_{0})";
    if (d==2)
      return "f''(x_{0})";
    if (d==3)
      return "f'''(x_{0})";
    return "f^{("+d+")}(x_{0})";
  }

  public String toClass() {
    return "Diff("+d+")";
  }
}

class RDec extends Exp {
  BigDecimal n;
  public RDec(){
	n = BigDecimal.ZERO;
	//n = BigDecimal.ONE;
  }
  public RDec(int a){
	n = BigDecimal.valueOf(a);
  }
  public RDec(BigInteger a){
	n = new BigDecimal(a);
  }
  public RDec(double a){
	n = BigDecimal.valueOf(a);
  }
  public RDec(BigDecimal a){
	n = a;
  }
  public Exp getE1() {
    return this;
  }

  public Exp getE2() {
    return this;
  }

  public Exp insert(Table t) {
    return new RDec(n);
  }
  
  public Exp reduce() {
    return this;
  }

  public Ratio eval(Table t) {
    return new Ratio(n);
  }
  
  public BigDecimal getDecimal() {
    return n;
  }

  public String toString(){
	return n + "";
  }
  public String toString2(){
	//	return  "$\\frac{"+sayi1+"}{"+sayi2+"}$";
    return n + "";
  }
  
  public String toLatex(boolean x) {
    return n + "";
  }

  public String toClass() {
    return "RDec("+n+")";
  }
}

class Ratio extends Exp {
  Exp e1, e2;
  public Ratio() {
	e1 = new RDec(0);
	e2 = new RDec(1);
  }
  public Ratio(int n) {
	e1 = new RDec(n);
	e2 = new RDec(1);
  }
  public Ratio(double n) {
	e1 = new RDec(n);
	e2 = new RDec(1);
  }
  public Ratio(Ratio a) {
	e1 = a.e1;
	e2 = a.e2;
  }
  public Ratio(Exp a) {
	e1 = a;
	e2 = new RDec(1);
  }
  public Ratio(Exp a, Exp b) {
	e1 = a;
	e2 = b;
  }
  public Ratio(BigInteger a) {
	e1 = new RDec(a);
	e2 = new RDec(1);
  }
  public Ratio(BigDecimal a) {
	e1 = new RDec(a);
	e2 = new RDec(1);
  }
  public Ratio(BigDecimal a, BigDecimal b) {
	e1 = new RDec(a);
	e2 = new RDec(b);
  }
  public Exp getE1() {
    return new Ratio(getR1());
  }
  public Exp getE2() {
    return new Ratio(getR2());
  }
  public BigDecimal getR1() {
    return e1.getDecimal();
  }
  public BigDecimal getR2() {
    return e2.getDecimal();
  }
  public Ratio getRatio(BigDecimal n1, BigDecimal n2) {
  	if(n2.signum() < 0) {  // compareTo(BigDecimal.ZERO)<0) {
  	  //n1 = new BigDecimal(-1).multiply(n1);
  	  //n2 = new BigDecimal(-1).multiply(n2);
  	  n1 = n1.negate();
  	  n2 = n2.negate();
  	}
  	return new Ratio(n1, n2);
  }
  public Ratio addR(Ratio a) {
  	BigDecimal n1 = getR1().multiply(a.getR2()).add(getR2().multiply(a.getR1()));
  	BigDecimal n2 = getR2().multiply(a.getR2());  	
  	return getRatio(n1, n2);
  }
  public Ratio subtractR(Ratio a) {
  	BigDecimal n1 = getR1().multiply(a.getR2()).subtract(getR2().multiply(a.getR1()));
  	BigDecimal n2 = getR2().multiply(a.getR2());
  	return getRatio(n1, n2);
  }
  public Ratio multiplyR(Ratio a) {
  	BigDecimal n1 = getR1().multiply(a.getR1());
  	BigDecimal n2 = getR2().multiply(a.getR2());
  	return getRatio(n1, n2);
  }
  public Ratio divideR(Ratio a) {
  	BigDecimal n1 = getR1().multiply(a.getR2());
  	BigDecimal n2 = getR2().multiply(a.getR1());
  	return getRatio(n1, n2);
  }
  public Ratio powR(Ratio a) { //a daima integer olmal�d�r
  	BigDecimal n1 = getR1().pow(a.getR1().intValue());
  	BigDecimal n2 = getR2().pow(a.getR1().intValue());
  	return getRatio(n1, n2);
  }
  public Ratio Simplify() {
  	try {
  	  BigInteger gcd = ebob(getR1().toBigIntegerExact().abs(), getR2().toBigIntegerExact());
	  e1 = new RDec(getR1().toBigInteger().divide(gcd));
	  e2 = new RDec(getR2().toBigInteger().divide(gcd));
  	}
  	catch (ArithmeticException exc) {
  	}
  	return this;
  }
  public Ratio abs() {
  	return new Ratio(getR1().abs(), getR2());
  }
  public BigInteger ebob(BigInteger a, BigInteger b) {
	while (!b.equals(BigInteger.ZERO)) {
	  BigInteger t = b;
	  b = a.mod(b);
	  a = t;
    }
    return a;
  }
  public BigInteger gcd(BigInteger a, BigInteger b) {
    if (b.equals(BigInteger.ZERO)) {
      return a;
    }
    else {
      return gcd(b, a.mod(b));
    }
  }
  public BigInteger lcm(BigInteger a) {
  	try {
  	  return getR2().toBigIntegerExact().multiply(a).divide(gcd(getR2().toBigIntegerExact(), a));
  	}
  	catch (Exception exc) {
  	  return getR2().toBigInteger();
  	}
  }
  public BigInteger lcm(BigInteger a, BigInteger b) {
    return a.multiply(b).divide(gcd(a, b));
  }
  public BigInteger lcm2(BigInteger[] numbers) {
    BigInteger result = numbers[0];
    for (int i = 1; i < numbers.length; i++) {
      result = lcm(result, numbers[i]);
    }
    return result;
  }
  public Exp insert(Table t) {
    return new Ratio(e1.insert(t), e2.insert(t));
  }
  public Exp reduce() {
    Exp e1 = this.e1.reduce();
    Exp e2 = this.e2.reduce();
    if (e1 instanceof Ratio && e2 instanceof Ratio)
      return ((Ratio)e1).divideR((Ratio)e2).Simplify();
    return  new Ratio(e1, e2).Simplify();
  }
  public Ratio eval(Table t) {
    return this;
  }
  public String toString(){
    if (getR1().equals(BigDecimal.ZERO))
      return e1 + "";
    else if (getR2().equals(BigDecimal.ONE))
      return e1 + "";
    else
      return e1 + "/" + e2;
  }
  public String toString2(){
    if (getR1().compareTo(BigDecimal.ZERO)==0)
      return e1 + "";
    else if (getR2().compareTo(BigDecimal.ONE)==0)
      return getR1() + "";
    else
      return getR1() + "/" + getR2();
  }
  public String toLatex(boolean x){
    if (!x)
      return e1.toLatex(x) + "";
    
    if (getR1().compareTo(BigDecimal.ZERO)==0)
      return e1.toLatex(x) + "";
    
    if (getR2().compareTo(BigDecimal.ONE)==0)
      return e1.toLatex(x) + "";
    
    return "\\frac{"+e1.toLatex(x)+"}{"+e2.toLatex(x)+"}";
  }
  public BigDecimal getDecimal() {
  	//return getR1().divide(getR2());
  	return getR1().divide(getR2(), 10, RoundingMode.DOWN);
  }
  public double getValue() {
  	return getDecimal().doubleValue();
  }
  public String toClass() {
    return "Ratio("+e1.toClass()+","+e2.toClass()+")";
  }
}
