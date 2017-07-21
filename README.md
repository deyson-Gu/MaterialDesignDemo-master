# MaterialDesignDemo-master
模仿华为计步界面的展示，可以修改展示圆角的角度
关键方法是
    //自定义圆角的角度
    private float cornerRadius = 10;
    
    //定义四个角的常量
    public static final int CORNER_TOP_LEFT = 1;    
    public static final int CORNER_TOP_RIGHT = 1 << 1;
    public static final int CORNER_BOTTOM_LEFT = 1 << 2;
    public static final int CORNER_BOTTOM_RIGHT = 1 << 3;
    public static final int CORNER_ALL = CORNER_TOP_LEFT | CORNER_TOP_RIGHT | CORNER_BOTTOM_LEFT | CORNER_BOTTOM_RIGHT;
    public static final int CORNER_ALL = CORNER_BOTTOM_LEFT | CORNER_BOTTOM_RIGHT;
    
    //传入画布和画笔，根据需要显示的要求，自定义对应的圆角的角度
    private void drawViewRect(Canvas canvas) {
        canvas.drawRoundRect(dataRectF, cornerRadius, cornerRadius, dataPaint);
        int notRoundedCorners =  CORNER_ALL;
        //哪个角不是圆角我再把你用矩形画出来
//        if ((notRoundedCorners & CORNER_TOP_LEFT) != 0) {
//            canvas.drawRect(0, 0, cornerRadius, cornerRadius, dataPaint);
//        }
//        if ((notRoundedCorners & CORNER_TOP_RIGHT) != 0) {
//            canvas.drawRect(dataRectF.right - cornerRadius, 0, dataRectF.right, cornerRadius, dataPaint);
//        }
        if ((notRoundedCorners & CORNER_BOTTOM_LEFT) != 0) {
            canvas.drawRect(dataRectF.left - cornerRadius, dataRectF.bottom - cornerRadius, cornerRadius, dataRectF.bottom, dataPaint);
        }
        if ((notRoundedCorners & CORNER_BOTTOM_RIGHT) != 0) {
            canvas.drawRect(dataRectF.right - cornerRadius, dataRectF.bottom - cornerRadius, dataRectF.right, dataRectF.bottom, dataPaint);
        }
    }
