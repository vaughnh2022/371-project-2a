package edu.luc.cs.laufer.cs371.shapes

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Shape.*

class TestHeight extends AnyFlatSpec with Matchers {
  
  "A Rectangle" should "return its height" in {
    val rect = Rectangle(200, 150)
    rect.height should be(150)
  }
  
  "An Ellipse" should "return its height" in {
    val ellipse = Ellipse(100, 75)
    ellipse.height should be(75)
  }
  
  "A bounding box" should "calculate correct height for a simple Rectangle" in {
    val rect = Rectangle(80, 120)
    val bbox = boundingBox(rect)
    val Rectangle(_, h) = bbox.shape
    h should be(120)
  }
  
  "A bounding box" should "calculate correct height for a simple Ellipse" in {
    val ellipse = Ellipse(100, 60)
    val bbox = boundingBox(ellipse)
    val Rectangle(_, h) = bbox.shape
    h should be(60)
  }
  
  "A bounding box" should "calculate correct height for a Located shape" in {
    val rect = Rectangle(80, 120)
    val located = Location(50, 30, rect)
    val bbox = boundingBox(located)
    val Rectangle(_, h) = bbox.shape
    h should be(120)
  }
  
  "A bounding box" should "have appropriate height for a Group" in {
    val group = Group(
      Rectangle(50, 50),
      Location(0, 100, Rectangle(50, 50))
    )
    val bbox = boundingBox(group)
    val Rectangle(_, h) = bbox.shape
    h should be(150)
  }
}
