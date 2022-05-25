public class Symbol{ 
  String type; 
  String kind; 
  int index; 
  
  Symbol( String type, String kind, int index) { 
    this.type = type; 
    this.kind = kind; 
    this.index = index;
  }
  
  String kindOf() { 
    return this.kind; 
  }
  
  String typeOf() { 
    return this.type; 
  }
  int indexOf() { 
    return this.index; 
  }
  
} 