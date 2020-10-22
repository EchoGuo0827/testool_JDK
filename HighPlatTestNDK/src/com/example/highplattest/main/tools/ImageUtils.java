package com.example.highplattest.main.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.example.highplattest.fragment.BaseFragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

/**
 * 请注意：目前底层解析只需需要YUV里面的Y数据，因此，UV目前填充为0
 * 
 * @hide
 *
 * @author weiyang
 *
 */
public class ImageUtils {
    private final static int WIDTH = 640;
    private final static int HEIGHT = 480;
    private final static int LENGTH = 640 * 480;
    private final static int DATA_LENGTH = 640 * 480 * 3 / 2;

    private final static String TAG = "ImageUtils";
    private static int isDebug = -1;
    private static String ON = "on";

    private static void debug(String str) {
        if (isDebug == -1) {
            String lang = BaseFragment.getProperty("persist.sys.nl_debug","-10086");
            if (ON.equals(lang)) {
                isDebug = 1;
            } else {
                isDebug = 0;
            }
        }
        if (isDebug == 1) {
            Log.d(TAG, str);
        }
    }

    /**
     * 裁剪图片，适用于图片宽高均大于宽高 camerade的预览数据为YUV420格式，具体是NV12和NV21 NV12；YYYYYYYY UVUV
     * NV21：YYYYYYYY VUVU 上述Y占w*h个数据， uv占w*h/2个数据
     *
     * @hide
     * @param yuv
     *            源图片数据
     * @param width
     *            源图片宽
     * @param height
     *            源图片高
     * @return 裁剪后的数据，按照标准640*480
     */
    public static byte[] cutImage(byte[] yuv, int width, int height) {
        debug("cutImage");
        if (width < WIDTH || height < HEIGHT) {
            return null;
        }
        int xOffset = (width - WIDTH) / 2;
        int yOffset = (height - HEIGHT) / 2;
        byte[] dest = new byte[HEIGHT * WIDTH * 3 / 2];
        for (int yCount = 0; yCount < HEIGHT; yCount++) {
            for (int xCount = 0; xCount < WIDTH; xCount++) {
                dest[yCount * WIDTH + xCount] = yuv[(yCount + yOffset) * width + (xCount + xOffset)];
            }
        }
        Arrays.fill(dest, LENGTH, DATA_LENGTH, (byte) 0);
        return dest;
    }

    /**
     * 扩充图片，不足之处用黑色填充， 适用于图片宽高均小于标准长度
     *
     * @hide
     * @param yuv
     *            源图片数据
     * @param width
     *            源图片宽
     * @param height
     *            源图片高
     * @return 裁剪后的数据，按照标准640*480
     */
    public static byte[] expandImage(byte[] yuv, int width, int height) {
        debug("expandImage");
        if (width > WIDTH || height > HEIGHT) {
            return null;
        }
        byte[] dest = new byte[HEIGHT * WIDTH * 3 / 2];
        int destCount = 0;
        for (int yCount = 0; yCount < HEIGHT; yCount++) {
            for (int xCount = 0; xCount < WIDTH; xCount++) {
                if (yCount >= height || xCount >= width) {
                    dest[destCount] = 0;
                } else {
                    dest[destCount] = yuv[xCount + yCount * width];
                }
                destCount++;
            }
        }
        Arrays.fill(dest, LENGTH, DATA_LENGTH, (byte) 0);
        return dest;
    }

    /**
     * 压缩图片,适用于长宽不一致的图片 注意，由于目前本司camera预览只有一个768*432,满足该选项 因此这里进行裁剪+扩充操作
     *
     * @hide
     * @param yuv
     *            源图片数据
     * @param width
     *            源图片宽度
     * @param height
     *            源图片高度
     * @return 压缩后的数据
     */
    public static byte[] compressImage(byte[] yuv, int width, int height) {
        debug("compressImage");
        byte[] dest = new byte[HEIGHT * WIDTH * 3 / 2];
        int destCount = 0;
        for (int yCount = 0; yCount < HEIGHT; yCount++) {
            for (int xCount = 0; xCount < WIDTH; xCount++) {
                if (yCount >= height || xCount >= width) {
                    dest[destCount] = 0;
                } else {
                    dest[destCount] = yuv[xCount + yCount * width];
                }
                destCount++;
            }
        }
        Arrays.fill(dest, LENGTH, DATA_LENGTH, (byte) 0);
        // debug( "compressImage = " + (System.currentTimeMillis() -
        // startTime));
        return dest;
    }

    // *************************************************************************//
    // ***************进行bitmap数据转yuv数据************************************//
    // ************************************************************************//
    // ***********************************************************************//
    /**
     * 将rgb数据转成压缩的yuv数据
     *
     * @hide
     * @param src
     *            源图片RGB格式数据
     * @return 压缩转换的yuv数据
     */
    public static byte[] compressRGBImage(byte[] src) {
        if (src == null || src.length == 0) {
            return null;
        }
        Bitmap bitmap = compressRgbPixels(src);
        if (bitmap == null) {
            debug("compress RGB byte to Bitmap failed!");
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > WIDTH || height > HEIGHT) {
            bitmap = compressBitmap(bitmap);
        }

        return getYuvDataFromBitmap(bitmap);
    }

    /**
     * 将Bitmap数据转成压缩的yuv数据
     * 
     * @param image
     * @return 压缩转换的yuv数据
     */
    public static byte[] compressRGBImage(Bitmap image) {
        if (image == null) {
            return null;
        }
        debug("compress bitmap:(" + image.getWidth() + "," + image.getHeight() + ")");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] rgb = baos.toByteArray();
        return compressRGBImage(rgb);
    }

    /**
     * 将InputStream数据转成压缩的yuv数据
     * 
     * @param inputStream
     * @return
     */
    public static byte[] compressRGBImage(InputStream inputStream) {
        int count = 0;
        byte[] rgb = null;
        try {
            while (count == 0) {
                count = inputStream.available();
            }
            rgb = new byte[count];
            inputStream.read(rgb);
        } catch (IOException e) {
            debug("compress inputStream to yuv data failed!");
            e.printStackTrace();
        }
        if (rgb == null) {
            return null;
        }
        return compressRGBImage(rgb);
    }

    /**
     * 将文件路径获取转成压缩的yuv数据
     *
     * @param filePath
     * @return
     */
    public static byte[] compressRGBImage(String filePath) {
        byte[] rgb = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            rgb = bos.toByteArray();
            return compressRGBImage(rgb);
        } catch (FileNotFoundException e) {
            debug("compress file to yuv data failed!");
            e.printStackTrace();
        } catch (IOException e) {
            debug("compress file to yuv data failed!");
            e.printStackTrace();
        } catch (Exception e) {
            debug("compress file to yuv data failed!");
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 图片太大，将图片压缩成640*480大小图片
     * 
     * @return
     */
    public static Bitmap compressBitmap(Bitmap origin) {
        // 获得图片的宽高
        int width = origin.getWidth();
        int height = origin.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) WIDTH) / width;
        float scaleHeight = ((float) HEIGHT) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newBm = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        gc(origin);
        return newBm;
    }

    /**
     * 对像素点压缩成bitmap
     * 
     * @param rgb
     * @return
     */
    private static Bitmap compressRgbPixels(byte[] rgb) {
        BitmapFactory.Options optioins = new BitmapFactory.Options();
        optioins.inJustDecodeBounds = true;
        optioins.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeByteArray(rgb, 0, rgb.length, optioins);
        optioins.inSampleSize = calculateInSampleSize(optioins, WIDTH, HEIGHT);
        if (optioins.outHeight == -1 || optioins.outWidth == -1) {
            optioins.inPreferredConfig = Bitmap.Config.ARGB_8888;
        }
        optioins.inJustDecodeBounds = false;
        // optioins.inPurgeable = true;
        // optioins.inInputShareable = true;
        return BitmapFactory.decodeByteArray(rgb, 0, rgb.length, optioins);
    }

    /**
     * 计算压缩的倍数，inSampleSize只能处理2次幂数据，如果传入的数据无法要求，则会改为最接近的2次幂数据
     * 
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 将bitmap转成 yuv数组
     * 
     * @param bitmap
     * @return
     */
    private static byte[] getYuvDataFromBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > WIDTH || height > HEIGHT) {
            width = WIDTH;
            height = HEIGHT;
        }
        int size = width * height;
        int pixels[] = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        gc(bitmap);
        byte yuv[] = rgb2NV12(pixels, width, height);
        if (width < WIDTH || height < HEIGHT) {
            yuv = expandImage(yuv, width, height);
        } else if (width > WIDTH || height > HEIGHT) {
            yuv = cutImage(yuv, width, height);
        }
        return yuv;
    }

    /**
     * rgb像素数组转yuv数组
     * 
     * @param src
     * @param width
     * @param height
     * @return
     */
    private static byte[] rgb2NV12(int[] src, int width, int height) {
        int length = src.length;
        byte[] yuv = new byte[length * 3 / 2];
        int y;// , u, v;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // 屏蔽ARGB的透明度值
                int rgb = src[i * width + j] & 0x00FFFFFF;
                // 像素的颜色顺序为bgr，移位运算。
                int r = rgb & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb >> 16) & 0xFF;
                // 套用公式
                y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                // u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
                // v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;
                // 调整
                y = y < 16 ? 16 : (y > 255 ? 255 : y);
                // u = u < 0 ? 0 : (u > 255 ? 255 : u);
                // v = v < 0 ? 0 : (v > 255 ? 255 : v);
                // 赋值
                yuv[i * width + j] = (byte) y;
                // yuv[length + (i >> 1) * width + (j & ~1) + 0] = (byte) u;
                // yuv[length + (i >> 1) * width + (j & ~1) + 1] = (byte) v;
            }
        }
        Arrays.fill(yuv, length, yuv.length, (byte) 0);
        return yuv;
    }

    /**
     * 回收图片
     * 
     * 
     * @param bitmap
     */
    private static void gc(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            // 回收并且置为null
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }

}
