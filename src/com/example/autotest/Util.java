package com.example.autotest;

final public class Util {

    public enum TestCode {
        LCDBackLight("屏全亮电流", 1001, true, null), 
        FlightMode("飞行模式打开关闭", 1002, true, null), 
        WIFI("WIFI打开关闭", 1003, true, null),
        GPS("GPS打开关闭", 1004, true, null), 
        Torch("手电筒", 1005, true, null),
        Vibrate("震动", 1006, true, null),
        Gesture("手势",1007, true, null),
        LocalVideo1("本地视频-720p", 1008, true, "/storage/emulated/0/DCIM/test_720p.mp4"),
        LocalVideo2("本地视频-1080p", 1009,true, "/storage/emulated/0/DCIM/test_1020p.mp4"),
        LocalVideo3("本地视频-4k*2k", 1010, true, "/storage/emulated/0/DCIM/test_4k*2k.mp4"),

        NormolMP3("正常模式MP3", 1011, true, null), 
        NormolMP3_h("正常模式MP3(标配耳机)", 1012, true, null),
        AirPlaneMP3("飞行模式MP3", 1013, true, null),
        AirPlaneMP3_h( "飞行模式MP3(标配耳机)", 1014, true, null),
        //AirPlaneMP3_bt("飞行模式MP3(蓝牙耳机)", 1015, true, null),

        RearCamera("后置摄像头-照相", 1016, true, null),
        RearCameraRecord("后置摄像头-录像", 1017,true, null),
        FrontCamer("前置摄像头-照相", 1018, true, null),
        FrontCamerRecord("前置摄像头-录像", 1019, true, null),

//        FMPlay("FM", 1020, false, "作为第三方应用此功能暂不可用"),

        Recoder("录音", 1021, true, null), 
        SD_W_R("SD卡读写", 1022, true, null),
        Browser_3G("3G-4G-wifi浏览网页", 1023, true, "http://weibo.cn/tcljituan"), // http://weibo.cn/tcljituan
        Browser_3G_video("3G播放视频", 1024, false, null), // http://imps.tcl-ta.com/qa/index.html/Mobile_Suit_Gundam_Unicorn_BDrip_05_BIG5_720P.mp4
        Browser_4G_video("4G播放视频", 1025, false, null),
        Browser_wifi_video("WIFI播放视频",1026, true, "http://imps.tcl-ta.com/cailiang/media/lq/3gp/h264_aac_320_240_v50_10_a16_22_m.mp4");

        private String name;
        private int Code;
        private boolean isChose;
        private String des;

        TestCode(String name, int code, boolean isChose, String des) {
            this.name = name;
            this.Code = code;
            this.isChose = isChose;
            this.des = des;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public int getCode() {
            return Code;
        }

        public boolean isChose() {
            return isChose;
        }

        public void setChose(boolean isChose) {
            this.isChose = isChose;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

    }

}
