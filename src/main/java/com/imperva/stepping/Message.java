package com.imperva.stepping;

 class Message {
     private final Data data;
     private final String subjectType;

     Message(Data data, String subjectType) {
         this.data = data;
         this.subjectType = subjectType;
     }

     Data getData() {
         return this.data;
     }

     String getSubjectType() {
         return this.subjectType;
     }

 }
