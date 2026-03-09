package edu.luc.cs.laufer.cs371.shapes

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Shape.*

class TestSize extends AnyFlatSpec with Matchers {
  
  "A Rectangle" should "have the correct width" in {
    val rect = Rectangle(100, 50)
    rect.width should be(100)
  }
  
  it should "have the correct height" in {
    val rect = Rectangle(100, 50)
    rect.height should be(50)
  }
  
  "An Ellipse" should "have the correct width" in {
    val ellipse = Ellipse(80, 60)
    ellipse.width should be(80)
  }
  
  it should "have the correct height" in {
    val ellipse = Ellipse(80, 60)
    ellipse.height should be(60)
  }
  
  "A Shape at Location" should "preserve the inner shape's size" in {
    val rect = Rectangle(100, 50)
    val located = Location(10, 20, rect)
    located.shape.asInstanceOf[Rectangle].width should be(100)
    located.shape.asInstanceOf[Rectangle].height should be(50)
  }
  
  "A Group" should "contain multiple shapes" in {
    val group = Group(Rectangle(50, 50), Ellipse(40, 40))
    group.shapes.length should be(2)
  }
}
