package custom.com.customskl.back;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import custom.com.customskl.R;

/**
 * Created by SKL on 2018/3/26.
 */

public class wheelView extends View {
    private float unitRadian = (float) (Math.PI / 180);
    private Paint mPaint_back;
    private Paint mPaint_back2;
    private Paint mPaint_center;

    private int radius_out = 300;
    private int radius_mid = 260;
    private int radius_in = 100;
    private int startAngle = 70;
    private int sweepAngle = 320;
    int width;
    int height;
//    private int[] sixColors = new int[]{R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5, R.color.color_6};
//    private int[] sixColors = new int[]{R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5, R.color.color_6};

    Region one, two, three, four, five, six, seven;
    Region globalRegion;

    public wheelView(Context context) {
        super(context);
    }

    Matrix mMapMatrix = null;

    public wheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMapMatrix = new Matrix();
        mPaint_back = new Paint();
        mPaint_back.setColor(getResources().getColor(R.color.gray_out));
        mPaint_back.setStrokeWidth(10);
        mPaint_back.setStyle(Paint.Style.FILL);
        mPaint_back.setAntiAlias(true);
        mPaint_back2 = new Paint();
        mPaint_back2.setColor(getResources().getColor(R.color.gray_int));
        mPaint_back2.setStrokeWidth(10);
        mPaint_back2.setStyle(Paint.Style.STROKE);
        mPaint_back2.setAntiAlias(true);
        mPaint_center = new Paint();
        mPaint_center.setColor(getResources().getColor(R.color.blue));
        mPaint_center.setAntiAlias(true);
        mPaint_center.setStyle(Paint.Style.FILL);

        one = new Region();
        two = new Region();
        three = new Region();
        four = new Region();
        five = new Region();
        six = new Region();
        seven = new Region();

    }

    public wheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMapMatrix.reset();
        height = h;
        width = w;
        globalRegion = new Region(-width, -height, width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(width / 2, height / 2);
        // 获取测量矩阵(逆矩阵)
        if (mMapMatrix.isIdentity()) {
            canvas.getMatrix().invert(mMapMatrix);
        }
        Paint_outcircle(canvas);//画外部圆
        Path p1 = Paint_outcircle_in(canvas);//画内圆
        List<Path> p = Paint_bottom(canvas);//画底部小圈
        Paint_sixArc(canvas, p, p1);//画6个扇形
    }


    /**
     * 画留个扇形
     *
     * @param canvas
     * @param paths
     * @param bse
     */
    private void Paint_sixArc(Canvas canvas, List<Path> paths, Path bse) {
        int flag = 0;
        for (int i = -90; i < 270; i += 60) {
            flag++;
            Path path1 = new Path();
            path1.addArc(new RectF(-radius_mid, -radius_mid, radius_mid, radius_mid), i, 60);
            PathMeasure pm = new PathMeasure(path1, false);
            float[] pos = new float[2];
            pm.getPosTan(0, pos, null);
            Path path2 = new Path();
            if (i == 90) {
                path2.lineTo(0, radius_mid - 50);
            } else {
                path2.lineTo(pos[0], pos[1]);
            }
            path1.lineTo(0, 0);
            Log.e("-----", "" + (radius_mid * Math.sin(i * unitRadian)) + "," + -(float) (radius_mid * Math.cos(i * unitRadian)));
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.white));
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            Paint paint2 = new Paint();
            paint2.setColor(getResources().getColor(R.color.gray_out));
            paint2.setStyle(Paint.Style.STROKE);

            if (i == -30 || i == 150) {
                path1.op(paths.get(0), Path.Op.DIFFERENCE);
            } else {
                path1.op(paths.get(1), Path.Op.DIFFERENCE);
            }
            path1.op(bse, Path.Op.INTERSECT);
            paint2.setAntiAlias(true);
            canvas.drawPath(path1, paint);
            if (i == -90) {
                paint2.setStrokeWidth(10);
            } else {
                paint2.setStrokeWidth(5);
            }
            if (i != 30 && i != 150) {
                canvas.drawPath(path2, paint2);
            }
            path1.op(paths.get(2), Path.Op.DIFFERENCE);
            canvas.drawPath(paths.get(2), mPaint_center);
            switch (flag) {
                case 1:
                    one.setPath(path1, globalRegion);
                    break;
                case 2:
                    two.setPath(path1, globalRegion);
                    break;
                case 3:
                    three.setPath(path1, globalRegion);
                    break;
                case 4:
                    four.setPath(path1, globalRegion);
                    break;
                case 5:
                    five.setPath(path1, globalRegion);
                    break;
                case 6:
                    six.setPath(path1, globalRegion);
                    break;
                case 7:
                    break;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int heightMeasureSpec) {
        int defaultvalue = 500;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int speceSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.AT_MOST) {
            //自己计算
        } else if (specMode == MeasureSpec.EXACTLY) {
            defaultvalue = speceSize;
        }
        return defaultvalue;

    }

    private int measureWidth(int widthMeasureSpec) {

        int defaultvalue = 500;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int speceSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.AT_MOST) {
            //自己计算
        } else if (specMode == MeasureSpec.EXACTLY) {
            defaultvalue = speceSize;
        }
        return defaultvalue;
    }

    private List<Path> Paint_bottom(Canvas canvas) {
        Path circle1 = new Path();
        circle1.addCircle(0, radius_out, radius_out, Path.Direction.CW);
        Path circle2 = new Path();
        circle2.addCircle(0, radius_mid - 40, radius_mid - 70, Path.Direction.CW);
        Path circle3 = new Path();
        circle3.addCircle(0, 0, radius_in, Path.Direction.CW);
        seven.setPath(circle3, globalRegion);
        Path result = new Path();
        result.op(circle1, circle2, Path.Op.DIFFERENCE);

        List<Path> a = new ArrayList<>();
        a.add(circle1);
        a.add(result);
        a.add(circle3);
        return a;
    }

    /**
     * 画外部圆形
     *
     * @param canvas
     */
    private void Paint_outcircle(Canvas canvas) {
        Path path = new Path();
        path.addArc(new RectF(-radius_out, -radius_out, radius_out, radius_out), startAngle, -sweepAngle);//里面那条
        PathMeasure pm = new PathMeasure(path, false);
        float[] pos = new float[2];
        pm.getPosTan(0, pos, null);
        float x1 = pos[0];
        float y1 = pos[1];

        pm.getPosTan(pm.getLength(), pos, null);
        float x2 = pos[0];
        float y2 = pos[1];
        Log.e("skl", "--------" + x1 + "," + y1 + "," + x2 + "," + y2);
        Path bse = new Path();
        bse.moveTo(x1, y1);
        bse.quadTo(0, 180, x2, y2);
        path.addPath(bse);
        path.setFillType(Path.FillType.EVEN_ODD);
        canvas.drawPath(path, mPaint_back);//一条封闭贝塞尔+圆弧
    }

    /**
     * 画内部圆形
     *
     * @param canvas
     */
    private Path Paint_outcircle_in(Canvas canvas) {
        Path path = new Path();
        path.addArc(new RectF(-radius_mid, -radius_mid, radius_mid, radius_mid), 65, -310);//里面那条
        PathMeasure pm = new PathMeasure(path, false);
        float[] pos = new float[2];
        pm.getPosTan(0, pos, null);
        float x1 = pos[0];
        float y1 = pos[1];

        pm.getPosTan(pm.getLength(), pos, null);
        float x2 = pos[0];
        float y2 = pos[1];
        Log.e("skl", "--------" + x1 + "," + y1 + "," + x2 + "," + y2);
        Path bse = new Path();
        bse.moveTo(x1, y1);
        bse.quadTo(0, 150, x2, y2);
        path.addPath(bse);
        path.setFillType(Path.FillType.EVEN_ODD);
//        canvas.drawPath(path, mPaint_back2);//一条封闭贝塞尔+圆弧
        return path;
    }

    //    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float[] pts = {event.getX(), event.getY()};
            mMapMatrix.mapPoints(pts);
            switch (ifwhere(pts)) {
                case "one":
                    Log.e("--------", ifwhere(pts));
                    break;
                case "two":
                    Log.e("--------", ifwhere(pts));
                    break;
                case "three":
                    Log.e("--------", ifwhere(pts));
                    break;
                case "four":
                    Log.e("--------", ifwhere(pts));
                    break;
                case "five":
                    Log.e("--------", ifwhere(pts));
                    break;
                case "six":
                    Log.e("--------", ifwhere(pts));
                    break;
                case "seven":
                    Log.e("--------", ifwhere(pts));
                    break;
                default:
                    break;
            }


        }
        return super.onTouchEvent(event);
    }

    private String ifwhere(float[] pts) {
        if (one.contains((int) pts[0], (int) pts[1])) {
            return "one";
        } else if (two.contains((int) pts[0], (int) pts[1])) {
            return "two";
        } else if (three.contains((int) pts[0], (int) pts[1])) {
            return "three";
        } else if (four.contains((int) pts[0], (int) pts[1])) {
            return "four";
        } else if (five.contains((int) pts[0], (int) pts[1])) {
            return "five";
        } else if (six.contains((int) pts[0], (int) pts[1])) {
            return "six";
        } else if (seven.contains((int) pts[0], (int) pts[1])) {
            return "seven";
        } else {
            return "";
        }
    }

}
