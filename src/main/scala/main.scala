import javax.swing._
import javax.swing.WindowConstants
import java.awt.{Graphics, Graphics2D, Color, BasicStroke, RenderingHints}
import java.awt.event.{MouseListener, MouseMotionListener, MouseEvent, KeyListener, KeyEvent}
import edu.luc.cs.laufer.cs371.shapes.*
import edu.luc.cs.laufer.cs371.shapes.Shape.*
import boundingBox.apply
import scala.collection.mutable

// Mode enumeration
enum Mode derives CanEqual:
  case DragMode
  case DrawRectMode
  case DrawEllipseMode

object Main {
  def main(args: Array[String]): Unit = {
    val frame = new JFrame("COMP 371 Project 2a")
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    frame.setSize(800, 600)
    
    // Create sample shapes with positions
    val initialShapes = scala.collection.mutable.ListBuffer(
      (Rectangle(80, 120), 50, 50),
      (Ellipse(100, 60), 50, 180),
      (Location(150, 100, Rectangle(100, 80)), 50, 320),
      (Group(Rectangle(50, 50), Location(80, 60, Ellipse(40, 40))), 50, 450)
    )
    
    val panel = new ShapePanel(initialShapes, frame.getContentPane.getWidth, frame.getContentPane.getHeight)
    frame.add(panel)
    
    frame.setLocationRelativeTo(null)
    frame.setVisible(true)
    
    // Keep the main thread alive so the window stays open
    Thread.sleep(Long.MaxValue)
  }
}

class ShapePanel(shapes: scala.collection.mutable.ListBuffer[(Shape, Int, Int)], windowWidth: Int, windowHeight: Int) extends JPanel {
  import Mode.*
  
  private var currentMode: Mode = DragMode
  
  // Drag mode variables
  private var draggedShapeIndex: Option[Int] = None
  private var dragOffsetX = 0
  private var dragOffsetY = 0
  
  // Draw mode variables
  private var isDrawing = false
  private var drawStartX = 0
  private var drawStartY = 0
  private var drawEndX = 0
  private var drawEndY = 0
  
  private var prevWindowWidth = windowWidth
  private var prevWindowHeight = windowHeight
  
  setFocusable(true)
  
  addMouseListener(new MouseListener {
    override def mousePressed(e: MouseEvent): Unit = {
      currentMode match {
        case DragMode =>
          val x = e.getX
          val y = e.getY
          
          // Check which shape was clicked (reverse iteration to click topmost shape)
          draggedShapeIndex = None
          for (i <- shapes.indices.reverse) {
            val (shape, shapeX, shapeY) = shapes(i)
            val bbox = boundingBox(shape)
            val bboxX = shapeX + bbox.x
            val bboxY = shapeY + bbox.y
            val Rectangle(w, h) = bbox.shape
            
            if (x >= bboxX && x <= bboxX + w && y >= bboxY && y <= bboxY + h) {
              draggedShapeIndex = Some(i)
              dragOffsetX = x - shapeX
              dragOffsetY = y - shapeY
            }
          }
        
        case DrawRectMode | DrawEllipseMode =>
          isDrawing = true
          drawStartX = e.getX
          drawStartY = e.getY
          drawEndX = e.getX
          drawEndY = e.getY
      }
    }
    
    override def mouseReleased(e: MouseEvent): Unit = {
      currentMode match {
        case DragMode =>
          draggedShapeIndex = None
        
        case DrawRectMode | DrawEllipseMode =>
          if (isDrawing) {
            drawEndX = e.getX
            drawEndY = e.getY
            
            // Only create shape if it has non-zero dimensions
            val width = Math.abs(drawEndX - drawStartX)
            val height = Math.abs(drawEndY - drawStartY)
            
            if (width > 5 && height > 5) {
              val newShape = currentMode match {
                case DrawRectMode => Rectangle(width, height)
                case DrawEllipseMode => Ellipse(width, height)
                case _ => Rectangle(width, height)
              }
              
              val startX = Math.min(drawStartX, drawEndX)
              val startY = Math.min(drawStartY, drawEndY)
              
              shapes += ((newShape, startX, startY))
            }
            
            isDrawing = false
            repaint()
          }
      }
    }
    
    override def mouseClicked(e: MouseEvent): Unit = {}
    override def mouseEntered(e: MouseEvent): Unit = {}
    override def mouseExited(e: MouseEvent): Unit = {}
  })
  
  addMouseMotionListener(new MouseMotionListener {
    override def mouseDragged(e: MouseEvent): Unit = {
      currentMode match {
        case DragMode =>
          draggedShapeIndex match {
            case Some(idx) =>
              val newX = e.getX - dragOffsetX
              val newY = e.getY - dragOffsetY
              
              val (shape, _, _) = shapes(idx)
              val bbox = boundingBox(shape)
              val Rectangle(w, h) = bbox.shape
              
              // Check boundaries: shape must stay within window and not collide with other shapes
              val minX = -bbox.x
              val maxX = getWidth - w - bbox.x
              val minY = -bbox.y
              val maxY = getHeight - h - bbox.y
              
              val constrainedX = math.max(minX, math.min(newX, maxX))
              val constrainedY = math.max(minY, math.min(newY, maxY))
              
              // Check for collisions with other shapes
              var collision = false
              for (i <- shapes.indices if i != idx) {
                val (otherShape, otherX, otherY) = shapes(i)
                val otherBbox = boundingBox(otherShape)
                val Rectangle(otherW, otherH) = otherBbox.shape
                
                val otherBboxX = otherX + otherBbox.x
                val otherBboxY = otherY + otherBbox.y
                
                val draggedBboxX = constrainedX + bbox.x
                val draggedBboxY = constrainedY + bbox.y
                
                // Check if bounding boxes overlap
                if (draggedBboxX < otherBboxX + otherW &&
                    draggedBboxX + w > otherBboxX &&
                    draggedBboxY < otherBboxY + otherH &&
                    draggedBboxY + h > otherBboxY) {
                  collision = true
                }
              }
              
              if (!collision) {
                shapes(idx) = (shape, constrainedX, constrainedY)
                repaint()
              }
            case None =>
          }
        
        case DrawRectMode | DrawEllipseMode =>
          drawEndX = e.getX
          drawEndY = e.getY
          repaint()
      }
    }
    
    override def mouseMoved(e: MouseEvent): Unit = {}
  })
  
  addKeyListener(new KeyListener {
    override def keyPressed(e: KeyEvent): Unit = {
      e.getKeyCode match {
        case KeyEvent.VK_D => currentMode = DrawRectMode; repaint()
        case KeyEvent.VK_E => currentMode = DrawEllipseMode; repaint()
        case KeyEvent.VK_A => currentMode = DragMode; repaint()
        case _ =>
      }
    }
    
    override def keyReleased(e: KeyEvent): Unit = {}
    override def keyTyped(e: KeyEvent): Unit = {}
  })
  
  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    val g2d = g.asInstanceOf[Graphics2D]
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    
    // Draw grid for reference
    g2d.setColor(new Color(200, 200, 200))
    g2d.setStroke(new BasicStroke(1))
    var x = 0
    while (x < getWidth) {
      g2d.drawLine(x, 0, x, getHeight)
      x += 50
    }
    var y = 0
    while (y < getHeight) {
      g2d.drawLine(0, y, getWidth, y)
      y += 50
    }
    
    for ((shape, shapeX, shapeY) <- shapes) {
      drawShape(g2d, shape, shapeX, shapeY)
      drawBoundingBox(g2d, boundingBox(shape), shapeX, shapeY)
    }
    
    // Draw preview for drawing mode
    val isInDrawMode = currentMode match {
      case DrawRectMode | DrawEllipseMode => true
      case _ => false
    }
    
    if (isDrawing && isInDrawMode) {
      val width = Math.abs(drawEndX - drawStartX)
      val height = Math.abs(drawEndY - drawStartY)
      val startX = Math.min(drawStartX, drawEndX)
      val startY = Math.min(drawStartY, drawEndY)
      
      g2d.setColor(new Color(0, 0, 0, 100))
      currentMode match {
        case DrawRectMode =>
          g2d.fillRect(startX, startY, width, height)
          g2d.setColor(Color.BLACK)
          g2d.setStroke(new BasicStroke(2))
          g2d.drawRect(startX, startY, width, height)
        case DrawEllipseMode =>
          g2d.fillOval(startX, startY, width, height)
          g2d.setColor(Color.BLACK)
          g2d.setStroke(new BasicStroke(2))
          g2d.drawOval(startX, startY, width, height)
        case _ =>
      }
    }
    
    // Draw mode indicator text
    g2d.setColor(Color.BLACK)
    g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14))
    val modeText = currentMode match {
      case DragMode => "Mode: DRAG (Press D=Rect, E=Ellipse, A=Arrow)"
      case DrawRectMode => "Mode: DRAW RECTANGLE (Press A=Arrow)"
      case DrawEllipseMode => "Mode: DRAW ELLIPSE (Press A=Arrow)"
    }
    g2d.drawString(modeText, 10, getHeight - 10)
  }
  
  private def drawShape(g: Graphics2D, s: Shape, offsetX: Int, offsetY: Int): Unit = s match {
    case Rectangle(w, h) =>
      g.setColor(Color.BLUE)
      g.fillRect(offsetX, offsetY, w, h)
      g.setColor(Color.BLACK)
      g.drawRect(offsetX, offsetY, w, h)
    case Ellipse(w, h) =>
      g.setColor(Color.GREEN)
      g.fillOval(offsetX, offsetY, w, h)
      g.setColor(Color.BLACK)
      g.drawOval(offsetX, offsetY, w, h)
    case Location(x, y, shape) =>
      drawShape(g, shape, offsetX + x, offsetY + y)
    case Group(shapes*) =>
      for (shape <- shapes) {
        drawShape(g, shape, offsetX, offsetY)
      }
  }
  
  private def drawBoundingBox(g: Graphics2D, bbox: Location, offsetX: Int, offsetY: Int): Unit = {
    g.setColor(Color.RED)
    g.setStroke(new BasicStroke(2))
    val Rectangle(w, h) = bbox.shape
    g.drawRect(offsetX + bbox.x, offsetY + bbox.y, w, h)
  }
}
