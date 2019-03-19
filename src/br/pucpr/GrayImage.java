package br.pucpr;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GrayImage implements Cloneable {
    private double[][] pixels;

    public static GrayImage toGray(BufferedImage img) {
        GrayImage out = new GrayImage(img.getWidth(), img.getHeight());
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color p = new Color(img.getRGB(x, y));
                out.pixels[x][y] =
                        0.30 * p.getRed() +
                        0.59 * p.getGreen() +
                        0.11 * p.getBlue();
            }
        }
        return out;
    }

    public static GrayImage load(String fileName) throws IOException {
        if (!fileName.contains("."))
            fileName = fileName + ".png";

        return toGray(ImageIO.read(new File(fileName)));
    }


    public GrayImage(int width, int height) {
        pixels = new double[width][height];
    }

    public double get(int x, int y) {
        return pixels[x][y];
    }

    public int getInt(int x, int y) {
        double t = Math.round(get(x, y));
        return (int) (t < 0.0 ? 0.0 : (t > 255.0 ? 255.0 : t));
    }

    public boolean isOn(int x, int y) {
        return get(x, y) >= 127.0;
    }

    public void set(int x, int y, double value) {
        if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) {
            return;
        }

        pixels[x][y] = value;
    }

    public void add(int x, int y, double value) {
        if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) {
            return;
        }
        pixels[x][y] += value;
    }

    public int getWidth() {
        return pixels.length;
    }

    public int getHeight() {
        return pixels[0].length;
    }

    private double findClosest(int[] pallete, double tone) {
        double closest = 0.0;
        double smallestError = Double.MAX_VALUE;

        for (int value : pallete) {
            double error = Math.abs(value - tone);
            if (error < smallestError) {
                smallestError = error;
                closest = value;
            }
        }

        return closest;
    }

    public GrayImage dither(int ... pallete) {
        GrayImage out = clone();

        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                double o = out.pixels[x][y];
                double t = findClosest(pallete, o);
                double e = o - t;

                out.set(x, y, t);
                out.add(x + 1,y + 0,e * 7.0 / 16.0);
                out.add(x - 1,y + 1,e * 3.0 / 16.0);
                out.add(x + 0,y + 1,e * 5.0 / 16.0);
                out.add(x + 1,y + 1,e * 1.0 / 16.0);
            }
        }
        return out;
    }

    public GrayImage shrink(int xSize, int ySize) {
        int nw = getWidth() / xSize;
        int nh = getHeight() / ySize;

        GrayImage out = new GrayImage(nw, nh);
        int ny = 0;

        for (int y = 0; y < getHeight(); y += ySize) {
            int nx = 0;
            for (int x = 0; x < getWidth(); x += xSize) {
                double t = 0.0;
                int c = 0;
                for (int j = 0; j < ySize; j++) {
                    for (int i = 0; i < xSize; i++) {
                        if (x+i >= getWidth() || y+j >= getHeight())
                            continue;
                        t += pixels[x+i][y+j];
                        c++;
                    }
                }
                t /= c;
                out.set(nx, ny, t);
                nx++;
            }
            ny++;
        }

        return out;
    }

    public GrayImage save(String fileName) throws IOException {
        BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                int t = getInt(x, y);
                img.setRGB(x, y, new Color(t,t,t).getRGB());
            }
        }
        ImageIO.write(img, "png", new File(fileName + ".png"));
        return this;
    }

    public GrayImage reverse() {
        GrayImage img = clone();
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                img.pixels[x][y] = 255.0 - img.pixels[x][y];
            }
        }
        return img;
    }

    @Override
    protected GrayImage clone() {
        GrayImage clone = new GrayImage(getWidth(), getHeight());
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                clone.pixels[x][y] = pixels[x][y];
            }
        }
        return clone;
    }
}
