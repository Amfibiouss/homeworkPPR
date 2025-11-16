package com.example.laba.services;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.lang.Math.abs;

//Сервис отвечающий за сжатие и урезание изображений
@Component
public class HandleImageService {

    public byte[] create_image(int R, int G, int B)  {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(R, G, B));
        g.fillRect(0, 0, 1, 1);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", out);
        } catch (IOException e) {
            // этого никогда не произойдет!
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }

    public byte[] cropping_scaling(byte[] photo) throws IOException {


        BufferedImage image = ImageIO.read(new ByteArrayInputStream(photo));

        int width = image.getWidth();
        int height = image.getHeight();
        int diff = abs(width - height);

        if (width > height) {
            image = image.getSubimage(diff/2, 0, width - diff, height);
        } else {
            image = image.getSubimage(0, diff/2, width, height - diff);
        }

        if (image.getWidth() > 480) {
            Image result = image.getScaledInstance(480, 480, Image.SCALE_DEFAULT);
            BufferedImage outputImage = new BufferedImage(480, 480, BufferedImage.TYPE_INT_RGB);
            outputImage.getGraphics().drawImage(result, 0, 0, null);
            image = outputImage;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        ImageIO.write(image, "jpg", out);

        return out.toByteArray();
    }

}
