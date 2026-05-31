package com.kuma.boot.seata.spi.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.ByteBuffer;
import org.apache.seata.common.loader.LoadLevel;
import org.apache.seata.common.loader.Scope;
import org.apache.seata.common.util.BufferUtils;
import org.apache.seata.core.protocol.AbstractMessage;
import org.apache.seata.core.serializer.Serializer;
import org.apache.seata.serializer.seata.MessageCodecFactory;
import org.apache.seata.serializer.seata.MessageSeataCodec;

@LoadLevel(
   name = "KMC",
   scope = Scope.PROTOTYPE
)
public class KmcSerializer implements Serializer {
   Serializer versionSeataSerializer;

   public KmcSerializer(Byte version) {
      if (version == 0) {
         this.versionSeataSerializer = KmcSerializer.SeataSerializerV0.getInstance();
      } else if (version == 1) {
         this.versionSeataSerializer = KmcSerializer.SeataSerializerV1.getInstance();
      }

      if (this.versionSeataSerializer == null) {
         throw new UnsupportedOperationException("version is not supported");
      }
   }

   public byte[] serialize(Object t) {
      return this.versionSeataSerializer.serialize(t);
   }

   @SuppressWarnings("unchecked")
   public <T> T deserialize(byte[] bytes) {
      return (T) this.versionSeataSerializer.deserialize(bytes);
   }

   private static Object deserializeByVersion(byte[] bytes, byte version) {
      if (bytes != null && bytes.length != 0) {
         if (bytes.length < 2) {
            throw new IllegalArgumentException("The byte[] isn't available for decode.");
         } else {
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            short typecode = byteBuffer.getShort();
            ByteBuffer in = byteBuffer.slice();
            AbstractMessage abstractMessage = MessageCodecFactory.getMessage(typecode);
            MessageSeataCodec messageCodec = MessageCodecFactory.getMessageCodec(typecode, version);
            messageCodec.decode(abstractMessage, in);
            return abstractMessage;
         }
      } else {
         throw new IllegalArgumentException("Nothing to decode.");
      }
   }

   static class SeataSerializerV1 implements Serializer {
      private static volatile SeataSerializerV1 instance;

      private SeataSerializerV1() {
      }

      public static SeataSerializerV1 getInstance() {
         if (instance == null) {
            synchronized(SeataSerializerV1.class) {
               if (instance == null) {
                  instance = new SeataSerializerV1();
               }
            }
         }

         return instance;
      }

      public byte[] serialize(Object t) {
         if (!(t instanceof AbstractMessage abstractMessage)) {
            throw new IllegalArgumentException("AbstractMessage isn't available.");
         } else {
            short typecode = abstractMessage.getTypeCode();
            MessageSeataCodec messageCodec = MessageCodecFactory.getMessageCodec(typecode, (byte)1);
            ByteBuf out = Unpooled.buffer(1024);
            messageCodec.encode(t, out);
            byte[] body = new byte[out.readableBytes()];
            out.readBytes(body);
            ByteBuffer byteBuffer = ByteBuffer.allocate(2 + body.length);
            byteBuffer.putShort(typecode);
            byteBuffer.put(body);
            BufferUtils.flip(byteBuffer);
            byte[] content = new byte[byteBuffer.limit()];
            byteBuffer.get(content);
            return content;
         }
      }

      @SuppressWarnings("unchecked")
      public <T> T deserialize(byte[] bytes) {
         return (T) KmcSerializer.deserializeByVersion(bytes, (byte)1);
      }
   }

   static class SeataSerializerV0 implements Serializer {
      private static volatile SeataSerializerV0 instance;

      private SeataSerializerV0() {
      }

      public static SeataSerializerV0 getInstance() {
         if (instance == null) {
            synchronized(SeataSerializerV0.class) {
               if (instance == null) {
                  instance = new SeataSerializerV0();
               }
            }
         }

         return instance;
      }

      public byte[] serialize(Object t) {
         if (!(t instanceof AbstractMessage abstractMessage)) {
            throw new IllegalArgumentException("AbstractMessage isn't available.");
         } else {
            short typecode = abstractMessage.getTypeCode();
            MessageSeataCodec messageCodec = MessageCodecFactory.getMessageCodec(typecode, (byte)0);
            ByteBuf out = Unpooled.buffer(1024);
            messageCodec.encode(t, out);
            byte[] body = new byte[out.readableBytes()];
            out.readBytes(body);
            ByteBuffer byteBuffer = ByteBuffer.allocate(body.length);
            byteBuffer.put(body);
            BufferUtils.flip(byteBuffer);
            byte[] content = new byte[byteBuffer.limit()];
            byteBuffer.get(content);
            return content;
         }
      }

      @SuppressWarnings("unchecked")
      public <T> T deserialize(byte[] bytes) {
         return (T) KmcSerializer.deserializeByVersion(bytes, (byte)0);
      }
   }
}
