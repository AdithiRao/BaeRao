import javafx.geometry.Point2D;
import java.io.Serializable;
public class Point implements Serializable
{
    private final double x;
    private final double y;
    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    public double getX()
    {
        return x;
    }
    public double getY()
    {
        return y;
    }
    public Point2D getJavaFXPoint2D()
    {
        return new Point2D(x, y);
    }
}
