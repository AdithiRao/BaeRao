import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import java.io.Serializable;
public class Curve extends ArrayList<Point> implements Serializable
{
    private final double width;
    private final double redLevel;
    private final double greenLevel;
    private final double blueLevel;

    public Curve(Color color, double width)
    {
        super();
        this.width = width;
        this.redLevel = color.getRed();
        this.greenLevel = color.getGreen();
        this.blueLevel = color.getBlue();
    }
    public void draw(GraphicsContext pen)
    {
        pen.setStroke(Color.rgb((int)(255*redLevel), (int)(255*greenLevel), (int)(255*blueLevel)));
        pen.setLineWidth(width);
        pen.setLineCap(StrokeLineCap.ROUND);
        pen.setLineJoin(StrokeLineJoin.ROUND);
        pen.beginPath();
        pen.moveTo(get(0).getX(), get(0).getY());
        for(int k = 0; k < size() - 1;  k++)
        {
            pen.lineTo(get(k+1).getX(), get(k+1).getY());
        }
        pen.stroke();
    }
}
