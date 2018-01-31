import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import java.util.ArrayList;
import javafx.scene.image.WritableImage;
import java.awt.image.RenderedImage;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.stage.FileChooser;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
public class BasicDraw extends Application
{
    private Stage primary;
    private ArrayList<Curve> drawing;
    private Curve currentCurve;
    private Color background;
    private Color currentColor;
    private double currentWidth;
    private MenuBar mbar;
    private final Canvas c;
    private File currentFile;
    private boolean isSaved;

    public BasicDraw()
    {
        drawing = new ArrayList<>();
        background = Color.WHITE;
        currentColor = Color.BLACK;
        currentWidth = 10;
        currentCurve = new Curve(currentColor, currentWidth);
        mbar = new MenuBar();
        c = new Canvas(600,600);
        currentFile = null;
        isSaved = false; //TODO:  Decide this thing's ultimate fate
    }
    @Override
    public void init()
    {
    }
    @Override
    public void start(Stage primary)
    {
        this.primary = primary;
        BorderPane bp = new BorderPane();
        bp.setCenter(c);
        bp.setTop(mbar);
        primary.setScene(new Scene(bp));
        GraphicsContext pen = c.getGraphicsContext2D();
        c.addEventHandler(MouseEvent.MOUSE_DRAGGED, e ->
        {
            currentCurve.add(new Point(e.getX(), e.getY()));
            refresh();
        });
        c.addEventHandler(MouseEvent.MOUSE_PRESSED, e ->
        {
            currentCurve = new Curve(currentColor, currentWidth);
            drawing.add(currentCurve);
            currentCurve.add(new Point(e.getX(), e.getY()));
        });
        buildMenus();
        refresh();
        primary.show();
    }
    @Override
    public void stop()
    {
    }
    private void refresh()
    {
        GraphicsContext pen = c.getGraphicsContext2D();
        pen.setFill(background);
        pen.fillRect(0,0, c.getWidth(), c.getHeight());
        for(Curve cur: drawing)
        {
            cur.draw(pen);
        }
    }
    private void buildMenus()
    {
        Menu fileMenu = new Menu("File");
        MenuItem newItem = new MenuItem("New");
        MenuItem openItem = new MenuItem("Open...");
        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction( e ->
        {
            //If the file is null, choose a file
            if(currentFile == null)
            {
                chooseFile();
            }
            //write file to disk
        });
        Menu saveAsItem = new Menu("Save As...");
        MenuItem quitItem = new MenuItem("Quit");
        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, quitItem);
        MenuItem asDWG = new MenuItem("DWG");
        MenuItem asGIF = new MenuItem("GIF");
        MenuItem asPNG = new MenuItem("PNG");
        MenuItem asJPEG = new MenuItem("JPEG");
        saveAsItem.getItems().addAll(asDWG, asGIF, asPNG, asJPEG);
        quitItem.setOnAction( e -> Platform.exit());
        Menu editMenu = new Menu("Edit");
        MenuItem clearItem = new MenuItem("Clear");
        MenuItem undoItem = new MenuItem("Undo Last Curve");
        asDWG.setOnAction( e ->
        {
            chooseFile();
            writeFileToDisk();
        });
        asPNG.setOnAction( e ->
        {
            saveCanvasToFileType("png");
        });
        asJPEG.setOnAction( e ->
        {
            saveCanvasToFileType("jpg");
        });
        asGIF.setOnAction( e ->
        {
            saveCanvasToFileType("gif");
        });
        editMenu.getItems().addAll(undoItem, clearItem);
        clearItem.setOnAction( e ->
        {
            drawing.clear();
            background=Color.WHITE;
            refresh();
        });
        undoItem.setOnAction( e ->
        {
            if(drawing.size() > 0)
            {
                drawing.remove(drawing.size() - 1);
            }
            refresh();
        });
        Menu colorMenu = new Menu("Color");
        ColorMenuItem dookItem = new ColorMenuItem("Dook", Color.rgb(0, 0x1A, 0x57));
        ColorMenuItem uncItem = new ColorMenuItem("Commie Hill", Color.rgb(0x99, 0xBA, 0xDD));
        ColorMenuItem ncsuItem = new ColorMenuItem("Rigor Mortis", Color.rgb(0xcc, 0, 0));
        colorMenu.getItems().addAll(dookItem, uncItem, ncsuItem );

        Menu bgMenu = new Menu("Background");
        BgMenuItem dookBgItem = new BgMenuItem("Dook", Color.rgb(0, 0x1A, 0x57));
        BgMenuItem uncBgItem = new BgMenuItem("Commie Hill", Color.rgb(0x99, 0xBA, 0xDD));
        BgMenuItem ncsuBgItem = new BgMenuItem("Rigor Mortis", Color.rgb(0xcc, 0, 0));
        bgMenu.getItems().addAll(dookBgItem, uncBgItem, ncsuBgItem);

        Menu widthMenu = new Menu("Width");
        WidthMenuItem one = new WidthMenuItem(1);
        WidthMenuItem two = new WidthMenuItem(2);
        WidthMenuItem five = new WidthMenuItem(5);
        WidthMenuItem ten = new WidthMenuItem(10);
        WidthMenuItem twenty = new WidthMenuItem(20);
        WidthMenuItem fifty = new WidthMenuItem(50);
        WidthMenuItem benjamin = new WidthMenuItem(100);
        widthMenu.getItems().addAll(one, two, five, ten, twenty, fifty, benjamin);

        mbar.getMenus().addAll(fileMenu, editMenu, colorMenu, bgMenu, widthMenu);

    }
    private void saveCanvasToFileType(String type)
    {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter =
            new FileChooser.ExtensionFilter(String.format("%s files (*.%s)",type, type),
                String.format("*.%s", type));
        fileChooser.getExtensionFilters().add(extFilter);

        currentFile = fileChooser.showSaveDialog(primary);

        if(currentFile != null){
            try
            {
                WritableImage writableImage =
                    new WritableImage((int)c.getWidth(), (int)c.getHeight());
                c.snapshot(null, writableImage);
                if(type.equals("jpg"))
                {
                    BufferedImage bImage = SwingFXUtils.fromFXImage(writableImage, null);
                    BufferedImage bImage2 = new BufferedImage(bImage.getWidth(),
                            bImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                    bImage2.getGraphics().drawImage(bImage, 0, 0, null);
                    ImageIO.write(bImage2, type, currentFile);
                    return;
                }
                RenderedImage renderedImage
                    = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, type, currentFile);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }

        }
    }
    private void chooseFile()
    {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
            new FileChooser.ExtensionFilter(".dwg file (*.dwg)", "*.dwg");
        fileChooser.getExtensionFilters().add(extFilter);
        currentFile = fileChooser.showSaveDialog(primary);
    }
    private void writeFileToDisk()
    {
        try
        {
          ObjectOutputStream oos =
          new ObjectOutputStream(new FileOutputStream(currentFile));
          oos.writeObject(drawing);
          oos.close();
        }
        catch(IOException ex)
        {
          System.err.println("An IOException has occured.");
          ex.printStackTrace();
        }
    }
    private void readFileFromDisk()
    {
    }

    class ColorMenuItem extends MenuItem
    {
        private final Color color;
        public ColorMenuItem(String name, Color color)
        {
            super(name);
            this.color = color;
            setOnAction(e ->
            {
                currentColor = color;
            });
        }
    }
    class BgMenuItem extends MenuItem
    {
        private final Color color;
        public BgMenuItem(String name, Color color)
        {
            super(name);
            this.color = color;
            setOnAction(e ->
            {
                background = color;
                refresh();
            });
        }
    }
    class WidthMenuItem extends MenuItem
    {
        private final double width;
        public WidthMenuItem(double width)
        {
            super("" + width);
            this.width = width;
            setOnAction(e ->
            {
                currentWidth = width;
            });
        }
    }
    public static void main(String[] args)
    {
        launch(args);
    }
}
