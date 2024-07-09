package com.app.integral_code.api;

import java.util.Hashtable;

class Table {
  Hashtable table = new Hashtable();

  public void put(String id, Exp e) {
  	table.put(id, e);
  }

  public Exp get(String id) {
  	return (Exp)table.get(id);
  }
}