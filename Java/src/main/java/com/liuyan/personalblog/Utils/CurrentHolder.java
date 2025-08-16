package com.liuyan.personalblog.Utils;

public class CurrentHolder {
    private static final ThreadLocal<Integer> tl = new ThreadLocal<>();
   public static void setCurrentId(Integer id){
        tl.set(id);

   }
   public static Integer getCurrentId(){
       return tl.get();
   }
   public static void remove(){
       tl.remove();
   }
}
