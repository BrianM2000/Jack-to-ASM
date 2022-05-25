import java.util.HashMap; 

public class SymbolTable { 
  
  HashMap<String, Symbol> classTable ; 
  HashMap<String, Symbol> subTable ; 
  int indField = 0; 
  int indStatic = 0; 
  int indVar = 0; 
  int indArg = 0 ;
  
  SymbolTable(){ 
    classTable  = new HashMap<String,Symbol> (); 
    subTable = new HashMap<String, Symbol>(); 
  }
  
  void add(String v,  String type, String kind){ 
    if (kind.equals("static" ) ) 
        classTable.put(v,new Symbol(type, kind, indStatic++));
    else if (kind.equals("field" ) ) 
       classTable.put(v,new Symbol(type, kind, indField++));
    else if (kind.equals("var" ) ) 
       subTable.put(v,new Symbol(type, kind, indVar++));
    else  if (kind.equals("arg" ) ) 
       subTable.put(v,new Symbol(type, kind, indArg++));
    
    else 
      System.out.println("error"); 
  }
  
  
  String typeOf(String name) { 
    if (subTable.containsKey(name)) 
      return subTable.get(name).typeOf(); 
    else 
      return classTable.get(name).typeOf(); 
  }
  
   String kindOf(String name) { 
    if (subTable.containsKey(name)) 
      return subTable.get(name).kindOf(); 
    else 
      return classTable.get(name).kindOf(); 
  }
   
  int indexOf(String name) { 
    if (subTable.containsKey(name)) 
      return subTable.get(name).indexOf(); 
    else 
      return classTable.get(name).indexOf(); 
  }
  
  int  varCount(String kind) { 
     int count = 0;
     if (kind.equals("static") || kind.equals("field")) 
       for (Symbol s : classTable.values()){
         if (s.kindOf().equals(kind)) count++;
       }
       
     if (kind.equals("var") || kind.equals("arg")) 
         for (Symbol s : subTable.values()){
         if (s.kindOf().equals(kind)) count++;
         }
 
      return count; 
   }
}