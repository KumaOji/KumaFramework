package com.kuma.boot.idgenerator.uid;

public class UidParser {
   public static final Position OID_CHANNEL_CODE = new Position(0, 7);
   public static final Position OID_PRODUCT_CODE = new Position(7, 12);
   public static final Position OID_BUSINESS_CODE = new Position(0, 12);
   public static final Position OID_SERVICE_CODE = new Position(12, 16);
   public static final Position OID_SYSTEM_CODE = new Position(16, 22);
   public static final Position OID_ENV_CODE = new Position(22, 24);
   public static final Position OID_DATA_CENTER_CODE = new Position(24, 25);
   public static final Position OID_SHARD_CODE = new Position(25, 27);
   public static final Position OID_EXTENSION_CODE = new Position(27, 30);
   public static final Position OID_SEQ_CODE = new Position(30, 46);
   public static final Position ID_SYSTEM_CODE = new Position(0, 6);
   public static final Position ID_ENV_CODE = new Position(6, 8);
   public static final Position ID_DATA_CENTER_CODE = new Position(8, 9);
   public static final Position ID_SHARD_CODE = new Position(9, 11);
   public static final Position ID_EXTENSION_CODE = new Position(11, 14);
   public static final Position ID_SEQ_CODE = new Position(14, 30);

   public UidParser() {
   }

   public static String parse(String uid, Position position) {
      return uid.substring(position.getFrom(), position.getTo());
   }

   public static class Position {
      int from;
      int to;

      public Position(int from, int to) {
         this.from = from;
         this.to = to;
      }

      public int getFrom() {
         return this.from;
      }

      public int getTo() {
         return this.to;
      }
   }
}
