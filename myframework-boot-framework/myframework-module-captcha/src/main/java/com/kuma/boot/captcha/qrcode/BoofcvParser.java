/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.captcha.qrcode;

import boofcv.abst.fiducial.QrCodeDetector;
import boofcv.alg.fiducial.qrcode.QrCode;
import boofcv.alg.fiducial.qrcode.QrCodeEncoder;
import boofcv.alg.fiducial.qrcode.QrCodeGeneratorImage;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 通过Boofcv 解析二维码图像
 */
public class BoofcvParser implements QRParser {

    @Override
    public boolean generate(String text, String filePath, String fileName) throws IOException {
        QrCode qr = new QrCodeEncoder()
                .setError(QrCode.ErrorLevel.M)
                .addAutomatic(text)
                .fixate();

        QrCodeGeneratorImage render = new QrCodeGeneratorImage(/* pixel per module */ 20);

        render.render(qr);

        // Convert it to a BufferedImage for display purposes
        BufferedImage image = ConvertBufferedImage.convertTo(render.getGray(), null);

        return ImageIO.write(image, "jpg", Files.newOutputStream(Paths.get(filePath + fileName)));
    }

    @Override
    public boolean generate(String text, String filePath, String fileName, BufferedImage image) {
        return true;
    }

    @Override
    public CharSequence parser(BufferedImage image) {
        return null;
    }

    @Override
    public CharSequence parser(String filePath, boolean isColorFul) throws IOException {
        BufferedImage image = ImageIO.read(new File(filePath));
        // 将BufferedImage转换为GrayU8图像
        GrayU8 input = ConvertBufferedImage.convertFrom(image, (GrayU8) null);

        // 创建QrCodeDetector实例
        QrCodeDetector<GrayU8> detector = FactoryFiducial.qrcode(null, GrayU8.class);

        // 检测二维码区域
        detector.process(input);

        List<QrCode> detections = detector.getDetections();
        StringBuilder sb = new StringBuilder();
        for (QrCode qr : detections) {
            // The message encoded in the marker
            sb.append(qr.message);
        }
        return sb.toString();
    }
}
