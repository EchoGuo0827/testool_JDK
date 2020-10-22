package com.example.highplattest.main.constant;

import java.util.HashMap;

import com.example.highplattest.main.bean.PaperInfo;


/**
 * ＊ 广州创自信息科技有限公司
 * Created by kavinwang on 16/9/21.
 */
public class PaperSizeConstants {
    public static final int EPS_CM_COLOR = 0;
    public static final int EPS_CM_MONOCHROME = 1;
    public static final int EPS_CM_SEPIA = 2;

    public static final int EPS_DUPLEX_NONE = 0;
    public static final int EPS_DUPLEX_LONG = 1;
    public static final int EPS_DUPLEX_SHORT = 2;

    public static final int EPS_FEEDDIR_PORTRAIT = 0;
    public static final int EPS_FEEDDIR_LANDSCAPE = 1;

    public static final int EPS_LANG_UNKNOWN = 0;
    public static final int EPS_LANG_ESCPR = 1;
    public static final int EPS_LANG_ESCPAGE = 2;
    public static final int EPS_LANG_ESCPAGE_COLOR = 3;
    public static final int EPS_LANG_ESCPAGE_S03 = 4;
    public static final int EPS_LANG_PCL_COLOR = 5;
    public static final int EPS_LANG_PCL = 6;
    public static final int EPS_LANG_ESCPAGE_S04 = 7;

    public static final int EPS_MLID_CUSTOM = 0;
    public static final int EPS_MLID_BORDERLESS = 1;
    public static final int EPS_MLID_BORDERS = 2;
    public static final int EPS_MLID_CDLABEL = 4;
    public static final int EPS_MLID_DIVIDE16 = 8;


    public static final int EPS_MPID_NOT_SPEC = 0;
    public static final int EPS_MPID_REAR = 1;
    public static final int EPS_MPID_FRONT1 = 2;
    public static final int EPS_MPID_FRONT2 = 4;
    public static final int EPS_MPID_CDTRAY = 8;
    public static final int EPS_MPID_REARMANUAL = 16;
    public static final int EPS_MPID_AUTO = 128;
    public static final int EPS_MPID_MPTRAY = 256;
    public static final int EPS_MPID_FRONT3 = 512;
    public static final int EPS_MPID_FRONT4 = 1024;
    public static final int EPS_MPID_ALL_ESCPAGE = 1798;
    public static final int EPS_MPID_ALL_ESCPR = 159;

    public static final int EPS_MQID_UNKNOWN = 0;
    public static final int EPS_MQID_DRAFT = 1;
    public static final int EPS_MQID_NORMAL = 2;
    public static final int EPS_MQID_HIGH = 4;
    public static final int EPS_MQID_ALL = 7;

    public static final int EPS_MSID_A4 = 0;
    public static final int EPS_MSID_LETTER = 1;
    public static final int EPS_MSID_LEGAL = 2;
    public static final int EPS_MSID_A5 = 3;
    public static final int EPS_MSID_A6 = 4;
    public static final int EPS_MSID_B5 = 5;
    public static final int EPS_MSID_EXECUTIVE = 6;
    public static final int EPS_MSID_HALFLETTER = 7;
    public static final int EPS_MSID_PANORAMIC = 8;
    public static final int EPS_MSID_TRIM_4X6 = 9;
    public static final int EPS_MSID_4X6 = 10;
    public static final int EPS_MSID_5X8 = 11;
    public static final int EPS_MSID_8X10 = 12;
    public static final int EPS_MSID_10X15 = 13;
    public static final int EPS_MSID_200X300 = 14;
    public static final int EPS_MSID_L = 15;
    public static final int EPS_MSID_POSTCARD = 16;
    public static final int EPS_MSID_DBLPOSTCARD = 17;
    public static final int EPS_MSID_ENV_10_L = 18;
    public static final int EPS_MSID_ENV_C6_L = 19;
    public static final int EPS_MSID_ENV_DL_L = 20;
    public static final int EPS_MSID_NEWEVN_L = 21;
    public static final int EPS_MSID_CHOKEI_3 = 22;
    public static final int EPS_MSID_CHOKEI_4 = 23;
    public static final int EPS_MSID_YOKEI_1 = 24;
    public static final int EPS_MSID_YOKEI_2 = 25;
    public static final int EPS_MSID_YOKEI_3 = 26;
    public static final int EPS_MSID_YOKEI_4 = 27;
    public static final int EPS_MSID_2L = 28;
    public static final int EPS_MSID_ENV_10_P = 29;
    public static final int EPS_MSID_ENV_C6_P = 30;
    public static final int EPS_MSID_ENV_DL_P = 31;
    public static final int EPS_MSID_NEWENV_P = 32;
    public static final int EPS_MSID_MEISHI = 33;
    public static final int EPS_MSID_BUZCARD_89X50 = 34;
    public static final int EPS_MSID_CARD_54X86 = 35;
    public static final int EPS_MSID_BUZCARD_55X91 = 36;
    public static final int EPS_MSID_ALBUM_L = 37;
    public static final int EPS_MSID_ALBUM_A5 = 38;
    public static final int EPS_MSID_PALBUM_L_L = 39;
    public static final int EPS_MSID_PALBUM_2L = 40;
    public static final int EPS_MSID_PALBUM_A5_L = 41;
    public static final int EPS_MSID_PALBUM_A4 = 42;
    public static final int EPS_MSID_HIVISION = 43;
    public static final int EPS_MSID_KAKU_2 = 44;
    public static final int EPS_MSID_ENV_C4_P = 45;
    public static final int EPS_MSID_A3NOBI = 61;
    public static final int EPS_MSID_A3 = 62;
    public static final int EPS_MSID_B4 = 63;
    public static final int EPS_MSID_USB = 64;
    public static final int EPS_MSID_11X14 = 65;
    public static final int EPS_MSID_B3 = 66;
    public static final int EPS_MSID_A2 = 67;
    public static final int EPS_MSID_USC = 68;
    public static final int EPS_MSID_10X12 = 69;
    public static final int EPS_MSID_12X12 = 70;
    public static final int EPS_MSID_USER = 99;
    public static final int EPS_MSID_UNKNOWN = 255;

    public static final int EPS_MTID_PLAIN = 0;
    public static final int EPS_MTID_360INKJET = 1;
    public static final int EPS_MTID_IRON = 2;
    public static final int EPS_MTID_PHOTOINKJET = 3;
    public static final int EPS_MTID_PHOTOADSHEET = 4;
    public static final int EPS_MTID_MATTE = 5;
    public static final int EPS_MTID_PHOTO = 6;
    public static final int EPS_MTID_PHOTOFILM = 7;
    public static final int EPS_MTID_MINIPHOTO = 8;
    public static final int EPS_MTID_OHP = 9;
    public static final int EPS_MTID_BACKLIGHT = 10;
    public static final int EPS_MTID_PGPHOTO = 11;
    public static final int EPS_MTID_PSPHOTO = 12;
    public static final int EPS_MTID_PLPHOTO = 13;
    public static final int EPS_MTID_MCGLOSSY = 14;
    public static final int EPS_MTID_ARCHMATTE = 15;
    public static final int EPS_MTID_WATERCOLOR = 16;
    public static final int EPS_MTID_PROGLOSS = 17;
    public static final int EPS_MTID_MATTEBOARD = 18;
    public static final int EPS_MTID_PHOTOGLOSS = 19;
    public static final int EPS_MTID_SEMIPROOF = 20;
    public static final int EPS_MTID_SUPERFINE2 = 21;
    public static final int EPS_MTID_DSMATTE = 22;
    public static final int EPS_MTID_CLPHOTO = 23;
    public static final int EPS_MTID_ECOPHOTO = 24;
    public static final int EPS_MTID_VELVETFINEART = 25;
    public static final int EPS_MTID_PROOFSEMI = 26;
    public static final int EPS_MTID_HAGAKIRECL = 27;
    public static final int EPS_MTID_HAGAKIINKJET = 28;
    public static final int EPS_MTID_PHOTOINKJET2 = 29;
    public static final int EPS_MTID_DURABRITE = 30;
    public static final int EPS_MTID_MATTEMEISHI = 31;
    public static final int EPS_MTID_HAGAKIATENA = 32;
    public static final int EPS_MTID_PHOTOALBUM = 33;
    public static final int EPS_MTID_PHOTOSTAND = 34;
    public static final int EPS_MTID_RCB = 35;
    public static final int EPS_MTID_PGPHOTOEG = 36;
    public static final int EPS_MTID_ENVELOPE = 37;
    public static final int EPS_MTID_PLATINA = 38;
    public static final int EPS_MTID_ULTRASMOOTH = 39;
    public static final int EPS_MTID_SFHAGAKI = 40;
    public static final int EPS_MTID_PHOTOSTD = 41;
    public static final int EPS_MTID_GLOSSYHAGAKI = 42;
    public static final int EPS_MTID_GLOSSYPHOTO = 43;
    public static final int EPS_MTID_GLOSSYCAST = 44;
    public static final int EPS_MTID_BUSINESSCOAT = 45;
    public static final int EPS_MTID_MEDICINEBAG = 46;
    public static final int EPS_MTID_THICKPAPER = 47;
    public static final int EPS_MTID_BROCHURE = 48;
    public static final int EPS_MTID_MATTE_DS = 49;
    public static final int EPS_MTID_BSMATTE_DS = 50;
    public static final int EPS_MTID_3D = 51;
    public static final int EPS_MTID_CDDVD = 91;
    public static final int EPS_MTID_CDDVDHIGH = 92;
    public static final int EPS_MTID_CDDVDGLOSSY = 93;
    public static final int EPS_MTID_CLEANING = 99;
    public static final int EPS_MTID_UNKNOWN = 255;

    protected final HashMap<Integer,PaperInfo> sCodeTable = new HashMap<Integer,PaperInfo>(){{
        put(EPS_MSID_A4,    new PaperInfo(2976, 4209, 2892, 4125, -36, -42, 3048, 4321));
        put(EPS_MSID_LETTER,new PaperInfo(3060, 3960, 2976, 3876, -36, -42, 3132, 4072));
        put(EPS_MSID_LEGAL, new PaperInfo(3060, 5040, 2976, 4956, -36, -42, 3132, 5152));
        put(EPS_MSID_A5,    new PaperInfo(2098, 2976, 2014, 2892, -36, -42, 2170, 3088));
        put(EPS_MSID_A6,    new PaperInfo(1488, 2098, 1404, 2014, -36, -42, 1560, 2210));
        put(EPS_MSID_B5,    new PaperInfo(2580, 3643, 2496, 3559, -36, -42, 2652, 3755));
        put(EPS_MSID_EXECUTIVE, new PaperInfo(2610, 3780, 2526, 3696, -36, -42, 2682, 3892));
        put(EPS_MSID_HALFLETTER, new PaperInfo(1980, 3060, 1896, 2976, -36, -42, 2052, 3172));
        put(EPS_MSID_PANORAMIC, new PaperInfo(2976, 8419, 2892, 8335, -36, -42, 3048, 8531));
        put(Integer.valueOf(9), new PaperInfo(1610, 2330, 1526, 2246, -36, -42, 1682, 2442));
        put(Integer.valueOf(10), new PaperInfo(1440, 2160, 1356, 2076, -36, -42, 1512, 2272));
        put(Integer.valueOf(11), new PaperInfo(1800, 2880, 1716, 2796, -36, -42, 1872, 2992));
        put(Integer.valueOf(12), new PaperInfo(2880, 3600, 2796, 3516, -36, -42, 2952, 3712));
        put(Integer.valueOf(13), new PaperInfo(1417, 2125, 1333, 2041, -36, -42, 1489, 2237));
        put(Integer.valueOf(14), new PaperInfo(3061, 4790, 2977, 4706, -36, -42, 3133, 4902));
        put(Integer.valueOf(15), new PaperInfo(1260, 1800, 1176, 1716, -36, -42, 1332, 1912));
        put(Integer.valueOf(16), new PaperInfo(1417, 2097, 1333, 2013, -36, -42, 1489, 2209));
        put(Integer.valueOf(17), new PaperInfo(2835, 2098, 2751, 2014, -36, -42, 2907, 2210));
        put(Integer.valueOf(18), new PaperInfo(3420, 1485, 3336, 1401, -36, -42, 3492, 1597));
        put(Integer.valueOf(19), new PaperInfo(2296, 1616, 2212, 1532, -36, -42, 2368, 1728));
        put(Integer.valueOf(20), new PaperInfo(3118, 1559, 3034, 1475, -36, -42, 3190, 1671));
        put(Integer.valueOf(21), new PaperInfo(3118, 1871, 3034, 1787, -36, -42, 3190, 1983));
        put(Integer.valueOf(22), new PaperInfo(1701, 3685, 1617, 3247, -36, -42, 1773, 3797));
        put(Integer.valueOf(23), new PaperInfo(1276, 3161, 1192, 2822, -36, -42, 1348, 3273));
        put(Integer.valueOf(24), new PaperInfo(1701, 2494, 1617, 2410, -36, -42, 1773, 2606));
        put(Integer.valueOf(25), new PaperInfo(1616, 2296, 1532, 2212, -36, -42, 1688, 2408));
        put(Integer.valueOf(26), new PaperInfo(1389, 2098, 1305, 2014, -36, -42, 1461, 2210));
        put(Integer.valueOf(27), new PaperInfo(1488, 3331, 1404, 3247, -36, -42, 1560, 3443));
        put(Integer.valueOf(28), new PaperInfo(1800, 2522, 1716, 2436, -36, -42, 1872, 2634));
        put(Integer.valueOf(29), new PaperInfo(1485, 3420, 1401, 3336, -36, -42, 1557, 3532));
        put(Integer.valueOf(30), new PaperInfo(1616, 2296, 1532, 2212, -36, -42, 1688, 2408));
        put(Integer.valueOf(31), new PaperInfo(1559, 3118, 1475, 3034, -36, -42, 1631, 3230));
        put(Integer.valueOf(32), new PaperInfo(1871, 3118, 1787, 3034, -36, -42, 1943, 3230));
        put(Integer.valueOf(33), new PaperInfo(1261, 779, 1177, 695, -36, -42, 1333, 891));
        put(Integer.valueOf(34), new PaperInfo(1261, 709, 1177, 625, -36, -42, 1333, 821));
        put(Integer.valueOf(35), new PaperInfo(765, 1219, 681, 1135, -36, -42, 837, 1331));
        put(Integer.valueOf(36), new PaperInfo(780, 1290, 696, 1206, -36, -42, 852, 1402));
        put(Integer.valueOf(37), new PaperInfo(1800, 2607, 1716, 2523, -36, -42, 1872, 2719));
        put(Integer.valueOf(38), new PaperInfo(2976, 4294, 2892, 4210, -36, -42, 3048, 4406));
        put(Integer.valueOf(39), new PaperInfo(1800, 1260, 1716, 1176, -36, -42, 1872, 1372));
        put(Integer.valueOf(40), new PaperInfo(1800, 2521, 1716, 2437, -36, -42, 1872, 2633));
        put(Integer.valueOf(41), new PaperInfo(2976, 2101, 2892, 2017, -36, -42, 3048, 2213));
        put(Integer.valueOf(42), new PaperInfo(2976, 4203, 2892, 4119, -36, -42, 3048, 4315));
        put(Integer.valueOf(43), new PaperInfo(1440, 2560, 1356, 2476, -36, -42, 1512, 2672));
        put(Integer.valueOf(44), new PaperInfo(2833, 3920, 2763, 3850, -30, -35, 2913, 4041));
        put(Integer.valueOf(45), new PaperInfo(2704, 3826, 2468, 3756, -30, -35, 2764, 3919));
        put(Integer.valueOf(61), new PaperInfo(4663, 6846, 4578, 6761, -48, -42, 4759, 6958));
        put(Integer.valueOf(62), new PaperInfo(4209, 5953, 4125, 5868, -48, -42, 4305, 6065));
        put(Integer.valueOf(63), new PaperInfo(3643, 5159, 3559, 5075, -36, -42, 3715, 5271));
        put(Integer.valueOf(64), new PaperInfo(3960, 6120, 3876, 6036, -36, -42, 4032, 6232));
        put(Integer.valueOf(65), new PaperInfo(3960, 5040, 3876, 4956, -36, -42, 4032, 5152));
        put(Integer.valueOf(66), new PaperInfo(5159, 7285, 5075, 7201, -48, -42, 5255, 7397));
        put(Integer.valueOf(67), new PaperInfo(5953, 8419, 5869, 8335, -48, -42, 6049, 8531));
        put(Integer.valueOf(68), new PaperInfo(6120, 7920, 6036, 7836, -48, -42, 6216, 8032));
        put(Integer.valueOf(69), new PaperInfo(3600, 4320, 3516, 4236, -36, -42, 3672, 4432));
        put(Integer.valueOf(70), new PaperInfo(4320, 4320, 4236, 4236, -36, -42, 4392, 4432));
        put(Integer.valueOf(102), new PaperInfo(0, 0, 0, 0, -36, -42, 0, 0));
        put(Integer.valueOf(128), new PaperInfo(5040, 6120, 4956, 6036, -36, -42, 5112, 6232));
        put(Integer.valueOf(129), new PaperInfo(5760, 7200, 5676, 7116, -36, -42, 5832, 7312));
        put(Integer.valueOf(255), new PaperInfo(0, 0, 0, 0, 0, 0, 0, 0));
    }};

    public PaperInfo getPaperInfoByCode(int code) {
        return this.sCodeTable.get(code);
    }

}
