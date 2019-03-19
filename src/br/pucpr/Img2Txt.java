package br.pucpr;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Img2Txt {
    private static final String GRAY_PALLETE = " .-=+*%@";

    private Map<Integer, Character> charMap = new HashMap<>();
    private int[] values;

    private BufferedImage charToImg(char letter) {
        BufferedImage img = new BufferedImage(9, 13, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Menlo", Font.PLAIN, 14));
        g2d.drawString("" + letter, 0, 10);
        g2d.dispose();
        return img;
    }

    private double whitePercent(BufferedImage img) {
        int on = 0;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (new Color(img.getRGB(x, y)).getRed() > 127) on = on + 1;
            }
        }
        return (double) on / (img.getWidth() * img.getHeight());
    }
    private void makePalette(String pallete) {
        charMap.clear();
        values = new int[pallete.length()];
        double[] lum = new double[pallete.length()];
        double maxLum = 0.0;

        //Draw each character in a String and calculate how many white pixels it has
        //The proportion of white / all is the estimated luminance
        for (int i = 0; i < pallete.length(); i++) {
            lum[i] = whitePercent(charToImg(pallete.charAt(i)));
            if (lum[i] > maxLum) maxLum = lum[i];
        }

        //Normalize the calculated luminance
        for (int i = 0; i < values.length; i++) {
            double l = (lum[i] / maxLum);
            values[i] = (int) Math.round(l * 255);

            System.out.printf("%s = %.2f (%d)%n", pallete.charAt(i), l, values[i]);
            charMap.put(values[i], pallete.charAt(i));
        }
    }

    public void toText1(GrayImage img) throws Exception {
        //Method 1.
        // a. Create a character palette based in char luminance (see makePallete method)
        // b. Shrink the image with 2x4 slots
        // c. Convert to 10 shades of gray with dither
        // d. Map them to the character pallete
        makePalette(GRAY_PALLETE);

        img = img.shrink(2, 4)
           .dither(values);

        try (PrintWriter out = new PrintWriter("toText1.txt")) {
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {

                    out.print(charMap.get(img.getInt(x, y)));
                }
                out.println();
            }
        }
    }

    public void toText2(GrayImage img) throws Exception {
        //Method 2
        //a. Shrink the image to with 1x2 slots
        //b. Binarize the imagem with dithering
        //c. Analyze the shape of a 2x2 pixel slot
        //d. Map to the closest letter format

        img = img.shrink(1, 2)
                .dither(0, 255);

        try (PrintWriter out = new PrintWriter("toText2.txt")) {
            for (int y = 0; y < img.getHeight() - 1; y += 2) {
                for (int x = 0; x < img.getWidth() - 1; x += 2) {
                    //p0 p1
                    //p2 p3
                    boolean p0 = img.isOn(x + 0, y + 0);
                    boolean p1 = img.isOn(x + 1, y + 0);
                    boolean p2 = img.isOn(x + 0, y + 1);
                    boolean p3 = img.isOn(x + 1, y + 1);

                    //0 on
                    if (!p0 && !p1 && !p2 && !p3) out.print(" ");

                    //1 on
                    else if (p0 && !p1 && !p2 && !p3) out.print("`");
                    else if (!p0 && p1 && !p2 && !p3) out.print("’");
                    else if (!p0 && !p1 && p2 && !p3) out.print(".");
                    else if (!p0 && !p1 && !p2 && p3) out.print(",");

                    //2 on
                    else if (p0 && p1 && !p2 && !p3) out.print("-");
                    else if (p0 && !p1 && p2 && !p3) out.print("{");
                    else if (p0 && !p1 && !p2 && p3) out.print("\\");
                    else if (!p0 && p1 && p2 && !p3) out.print("/");
                    else if (!p0 && p1 && !p2 && p3) out.print("}");
                    else if (!p0 && !p1 && p2 && p3) out.print("_");

                    //3 on
                    else if (p0 && p1 && p2 && !p3) out.print("F");
                    else if (p0 && p1 && !p2 && p3) out.print("?");
                    else if (p0 && !p1 && p2 && p3) out.print("L");
                    else if (!p0 && p1 && p2 && p3) out.print("J");

                    //4 on
                    else out.print("#");
                }
                out.println();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Img2Txt app = new Img2Txt();
        String name = args.length == 0 ? "images/jobs.jpg" : args[0];

        //Calls reverse() for white background generation (e.g. github or notepad)
        //Remove reverse() for black background (e.g. terminal or intellij darcula theme)
        GrayImage img = GrayImage.load(name).reverse();
        app.toText1(img);
        app.toText2(img);
    }
}
