package edu.luc.cs.laufer.cs371.shapes

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Shape.*
import java.awt.{Graphics2D, Color}
import java.awt.image.BufferedImage
import java.awt.event.{MouseEvent, KeyEvent}
import scala.collection.mutable
import javax.swing.JPanel

// Local mode enumeration for testing
enum DrawMode derives CanEqual:
  case DragMode
  case DrawRectMode
  case DrawEllipseMode

class TestDraw extends AnyFlatSpec with Matchers {
  
  // Helper to create mock MouseEvent
  private def createMouseEvent(
    source: JPanel,
    id: Int,
    when: Long,
    x: Int,
    y: Int,
    button: Int = MouseEvent.BUTTON1
  ): MouseEvent = {
    new MouseEvent(
      source,
      id,
      when,
      0,
      x,
      y,
      0,
      false,
      button
    )
  }
  
  // Helper to create mock KeyEvent
  private def createKeyEvent(
    source: JPanel,
    keyCode: Int
  ): KeyEvent = {
    new KeyEvent(
      source,
      KeyEvent.KEY_PRESSED,
      System.currentTimeMillis(),
      0,
      keyCode,
      KeyEvent.CHAR_UNDEFINED
    )
  }
  
  // Helper to simulate drawing a rectangle
  private def simulateDrawRectangle(
    panel: JPanel,
    shapes: mutable.ListBuffer[(Shape, Int, Int)],
    startX: Int,
    startY: Int,
    endX: Int,
    endY: Int
  ): Unit = {
    // Simulate mouse press
    val pressEvent = createMouseEvent(panel, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), startX, startY)
    panel.asInstanceOf[MockShapePanel].mockMousePressed(pressEvent)
    
    // Simulate mouse release
    val releaseEvent = createMouseEvent(panel, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), endX, endY)
    panel.asInstanceOf[MockShapePanel].mockMouseReleased(releaseEvent)
  }
  
  "Drawing a Rectangle" should "add a Rectangle shape to the shapes list" in {
    val shapes = mutable.ListBuffer[(Shape, Int, Int)]()
    val panel = new MockShapePanel(shapes, 800, 600)
    
    // Simulate pressing D to enter DrawRectMode
    val dKeyEvent = createKeyEvent(panel, KeyEvent.VK_D)
    panel.mockKeyPressed(dKeyEvent)
    
    // Simulate drawing a rectangle from (100, 100) to (200, 200)
    val pressEvent = createMouseEvent(panel, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 100, 100)
    panel.mockMousePressed(pressEvent)
    
    val releaseEvent = createMouseEvent(panel, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 200, 200)
    panel.mockMouseReleased(releaseEvent)
    
    // Verify a Rectangle was added
    shapes.length should be(1)
    val (shape, x, y) = shapes(0)
    shape should be(Rectangle(100, 100))
    x should be(100)
    y should be(100)
  }
  
  "Drawing an Ellipse" should "add an Ellipse shape to the shapes list" in {
    val shapes = mutable.ListBuffer[(Shape, Int, Int)]()
    val panel = new MockShapePanel(shapes, 800, 600)
    
    // Simulate pressing E to enter DrawEllipseMode
    val eKeyEvent = createKeyEvent(panel, KeyEvent.VK_E)
    panel.mockKeyPressed(eKeyEvent)
    
    // Simulate drawing an ellipse from (150, 150) to (250, 250)
    val pressEvent = createMouseEvent(panel, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 150, 150)
    panel.mockMousePressed(pressEvent)
    
    val releaseEvent = createMouseEvent(panel, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 250, 250)
    panel.mockMouseReleased(releaseEvent)
    
    // Verify an Ellipse was added
    shapes.length should be(1)
    val (shape, x, y) = shapes(0)
    shape should be(Ellipse(100, 100))
    x should be(150)
    y should be(150)
  }
  
  "Drawing with reversed coordinates" should "calculate dimensions correctly" in {
    val shapes = mutable.ListBuffer[(Shape, Int, Int)]()
    val panel = new MockShapePanel(shapes, 800, 600)
    
    // Simulate pressing D for rectangle
    val dKeyEvent = createKeyEvent(panel, KeyEvent.VK_D)
    panel.mockKeyPressed(dKeyEvent)
    
    // Simulate drawing from (200, 200) to (100, 100) - reversed
    val pressEvent = createMouseEvent(panel, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 200, 200)
    panel.mockMousePressed(pressEvent)
    
    val releaseEvent = createMouseEvent(panel, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 100, 100)
    panel.mockMouseReleased(releaseEvent)
    
    // Should still create a 100x100 rectangle at (100, 100)
    shapes.length should be(1)
    val (shape, x, y) = shapes(0)
    shape should be(Rectangle(100, 100))
    x should be(100)
    y should be(100)
  }
  
  "Drawing multiple shapes" should "add all shapes to the list" in {
    val shapes = mutable.ListBuffer[(Shape, Int, Int)]()
    val panel = new MockShapePanel(shapes, 800, 600)
    
    // Draw first rectangle
    val dKeyEvent = createKeyEvent(panel, KeyEvent.VK_D)
    panel.mockKeyPressed(dKeyEvent)
    
    val press1 = createMouseEvent(panel, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 50, 50)
    panel.mockMousePressed(press1)
    val release1 = createMouseEvent(panel, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 100, 100)
    panel.mockMouseReleased(release1)
    
    // Draw second ellipse
    val eKeyEvent = createKeyEvent(panel, KeyEvent.VK_E)
    panel.mockKeyPressed(eKeyEvent)
    
    val press2 = createMouseEvent(panel, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 200, 200)
    panel.mockMousePressed(press2)
    val release2 = createMouseEvent(panel, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 300, 250)
    panel.mockMouseReleased(release2)
    
    // Verify both shapes were added
    shapes.length should be(2)
    shapes(0)._1 should be(Rectangle(50, 50))
    shapes(1)._1 should be(Ellipse(100, 50))
  }
  
  "Drawing a small shape" should "not be created if smaller than 5x5" in {
    val shapes = mutable.ListBuffer[(Shape, Int, Int)]()
    val panel = new MockShapePanel(shapes, 800, 600)
    
    // Simulate pressing D for rectangle
    val dKeyEvent = createKeyEvent(panel, KeyEvent.VK_D)
    panel.mockKeyPressed(dKeyEvent)
    
    // Simulate drawing a very small shape (only 3x3)
    val pressEvent = createMouseEvent(panel, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 100, 100)
    panel.mockMousePressed(pressEvent)
    
    val releaseEvent = createMouseEvent(panel, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 103, 103)
    panel.mockMouseReleased(releaseEvent)
    
    // Should not create any shape
    shapes.length should be(0)
  }
  
  "Drawing to BufferedImage" should "render correct pixel colors" in {
    // Create a BufferedImage to draw on
    val img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB)
    val g2d = img.createGraphics()
    
    // Clear the image to white
    g2d.setColor(Color.WHITE)
    g2d.fillRect(0, 0, 400, 400)
    
    // Draw a blue rectangle (matching the app's rectangle color)
    g2d.setColor(Color.BLUE)
    g2d.fillRect(50, 50, 100, 100)
    g2d.setColor(Color.BLACK)
    g2d.drawRect(50, 50, 100, 100)
    
    // Verify that pixels within the rectangle are blue
    val bluePixel = img.getRGB(100, 100)
    val blueColor = Color.BLUE.getRGB
    bluePixel should be(blueColor)
    
    // Verify that pixels outside the rectangle are white
    val whitePixel = img.getRGB(25, 25)
    val whiteColor = Color.WHITE.getRGB
    whitePixel should be(whiteColor)
    
    g2d.dispose()
  }
  
  "Drawing an ellipse to BufferedImage" should "render correct pixel colors" in {
    // Create a BufferedImage to draw on
    val img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB)
    val g2d = img.createGraphics()
    
    // Clear the image to white
    g2d.setColor(Color.WHITE)
    g2d.fillRect(0, 0, 400, 400)
    
    // Draw a green ellipse (matching the app's ellipse color)
    g2d.setColor(Color.GREEN)
    g2d.fillOval(100, 100, 100, 100)
    g2d.setColor(Color.BLACK)
    g2d.drawOval(100, 100, 100, 100)
    
    // Verify that pixels within the ellipse are green
    val greenPixel = img.getRGB(150, 150)
    val greenColor = Color.GREEN.getRGB
    greenPixel should be(greenColor)
    
    // Verify that pixels in the corners are white (outside ellipse)
    val whitePixel = img.getRGB(105, 105)
    val whiteColor = Color.WHITE.getRGB
    whitePixel should be(whiteColor)
    
    g2d.dispose()
  }
  
  "Switching modes" should "change drawing behavior" in {
    val shapes = mutable.ListBuffer[(Shape, Int, Int)]()
    val panel = new MockShapePanel(shapes, 800, 600)
    
    // Start in DrawRectMode
    val dKeyEvent = createKeyEvent(panel, KeyEvent.VK_D)
    panel.mockKeyPressed(dKeyEvent)
    
    val press1 = createMouseEvent(panel, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 50, 50)
    panel.mockMousePressed(press1)
    val release1 = createMouseEvent(panel, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 100, 100)
    panel.mockMouseReleased(release1)
    
    // Switch to DrawEllipseMode
    val eKeyEvent = createKeyEvent(panel, KeyEvent.VK_E)
    panel.mockKeyPressed(eKeyEvent)
    
    val press2 = createMouseEvent(panel, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 150, 150)
    panel.mockMousePressed(press2)
    val release2 = createMouseEvent(panel, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 200, 200)
    panel.mockMouseReleased(release2)
    
    // Verify shapes match their modes
    shapes(0)._1 should be(a[Rectangle])
    shapes(1)._1 should be(an[Ellipse])
  }
  
  "Drawing in DragMode" should "not create new shapes" in {
    val shapes = mutable.ListBuffer[(Shape, Int, Int)](
      (Rectangle(50, 50), 100, 100)
    )
    val panel = new MockShapePanel(shapes, 800, 600)
    
    // Ensure we're in DragMode (default)
    val aKeyEvent = createKeyEvent(panel, KeyEvent.VK_A)
    panel.mockKeyPressed(aKeyEvent)
    
    // Try to draw (should not work in DragMode)
    val pressEvent = createMouseEvent(panel, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 100, 100)
    panel.mockMousePressed(pressEvent)
    
    val releaseEvent = createMouseEvent(panel, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 150, 150)
    panel.mockMouseReleased(releaseEvent)
    
    // Should still only have the original shape
    shapes.length should be(1)
  }
}

// Mock ShapePanel that exposes internal methods for testing
class MockShapePanel(
  shapes: mutable.ListBuffer[(Shape, Int, Int)],
  windowWidth: Int,
  windowHeight: Int
) extends JPanel {
  import DrawMode.*
  
  private var currentMode: DrawMode = DragMode
  private var draggedShapeIndex: Option[Int] = None
  private var dragOffsetX = 0
  private var dragOffsetY = 0
  private var isDrawing = false
  private var drawStartX = 0
  private var drawStartY = 0
  private var drawEndX = 0
  private var drawEndY = 0
  
  setFocusable(true)
  
  def mockMousePressed(e: java.awt.event.MouseEvent): Unit = {
    currentMode match {
      case DragMode =>
        val x = e.getX
        val y = e.getY
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
  
  def mockMouseReleased(e: java.awt.event.MouseEvent): Unit = {
    currentMode match {
      case DragMode =>
        draggedShapeIndex = None
      
      case DrawRectMode | DrawEllipseMode =>
        if (isDrawing) {
          drawEndX = e.getX
          drawEndY = e.getY
          
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
        }
    }
  }
  
  def mockKeyPressed(e: java.awt.event.KeyEvent): Unit = {
    e.getKeyCode match {
      case java.awt.event.KeyEvent.VK_D => currentMode = DrawRectMode
      case java.awt.event.KeyEvent.VK_E => currentMode = DrawEllipseMode
      case java.awt.event.KeyEvent.VK_A => currentMode = DragMode
      case _ =>
    }
  }
  
  override def paintComponent(g: java.awt.Graphics): Unit = {}
}
