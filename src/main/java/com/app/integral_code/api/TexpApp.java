package com.app.integral_code.api;

import java.math.BigDecimal;

public class TexpApp {
    public TexpApp() {}

    public Texp getInteg (int [] m) {
        PInteg fd = new PInteg(m);

        Texp[] e = fd.polynomial();
        Exp[][] e5 = fd.coeffMatrix(e);
        Exp[][] e6 = fd.integMatrix(e5);

        //Asagiya dogru indirge
        for (int i=0; i<e6.length-1; i++) {
            for (int j=i+1; j<e6.length; j++) {
                e6 = fd.gaussMatrix(e6, i, j);
            }
        }

        //Yukariya dogru indirge
        for (int i=e6.length-1; i>0; i--) {
            for (int j=i-1; j>=0; j--) {
                e6 = fd.gaussMatrix(e6, i, j);
            }
        }

        //Birim matrise donustur
        e6 = fd.identityMatrix(e6);

        Table tb = new Table();
        tb.put("x", new Id((e.length-1)+"h"));

        Exp p1 = fd.polynomial_integ();
        Texp pe = new Texp(new Id("I"), p1.insert(tb));

        tb.put("x", new Ratio(e.length-1));
        for (int i=0; i<e6.length; i++) {
            tb.put("c"+i, e6[i][e6.length]);
        }

        Texp pe2 = new Texp(new Id("I"), new Times (new Id("h"), new Par(p1.insert(tb))));
        Texp pe3 = new Texp(new Id("I"), new Times(new Id("h"), new Par(pe2.getE2().getE2().reduce())));

        if (pe3.getE2().getE2().getE2() instanceof Poly) {
            Poly p = (Poly)pe3.getE2().getE2().getE2();
            if (p.ps[0].getDecimal().compareTo(BigDecimal.ZERO)!=0 && p.ps[0].getDecimal().compareTo(BigDecimal.ONE)!=0) {
                Ratio n = p.ps[0];
                p = p.divideP(n);
                pe3 = new Texp(new Id("I"), new Times(new Times(n, new Id("h")), new Par(p)));
            }
        }

        return pe3;
    }
}