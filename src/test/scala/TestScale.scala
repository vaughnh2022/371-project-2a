package edu.luc.cs.laufer.cs371.shapes

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Shape.*

class TestScale extends AnyFlatSpec with Matchers {
  
  private def scaleShape(s: Shape, factor: Double): Shape = s match {
    case Rectangle(w, h) => Rectangle((w * factor).toInt, (h * factor).toInt)
    case Ellipse(w, h) => Ellipse((w * factor).toInt, (h * factor).toInt)
    case Location(x, y, shape) => Location(x, y, scaleShape(shape, factor))
    case Group(shapes*) => Group(shapes.map(scaleShape(_, factor)): _*)
  }
  
  "A Rectangle" should "scale correctly by factor 2" in {
    val rect = Rectangle(100, 50)
    val scaled = scaleShape(rect, 2.0)
    scaled should be(Rectangle(200, 100))
  }
  
  "A Rectangle" should "scale correctly by factor 0.5" in {
    val rect = Rectangle(100, 50)
    val scaled = scaleShape(rect, 0.5)
    scaled should be(Rectangle(50, 25))
  }
  
  "An Ellipse" should "scale correctly by factor 2" in {
    val ellipse = Ellipse(80, 60)
    val scaled = scaleShape(ellipse, 2.0)
    scaled should be(Ellipse(160, 120))
  }
  
  "A Located shape" should "scale the inner shape but preserve location" in {
    val located = Location(10, 20, Rectangle(100, 50))
    val scaled = scaleShape(located, 2.0)
    scaled should be(Location(10, 20, Rectangle(200, 100)))
  }
  
  "A Group" should "scale all contained shapes" in {
    val group = Group(Rectangle(100, 50), Ellipse(80, 60))
    val scaled = scaleShape(group, 2.0)
    scaled should be(Group(Rectangle(200, 100), Ellipse(160, 120)))
  }
  
  "A scaled shape" should "have correct bounding box" in {
    val rect = Rectangle(100, 50)
    val scaled = scaleShape(rect, 2.0)
    val bbox = boundingBox(scaled)
    val Rectangle(w, h) = bbox.shape
    w should be(200)
    h should be(100)
  }
}
