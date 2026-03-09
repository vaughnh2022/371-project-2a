package edu.luc.cs.laufer.cs371.shapes

// TODO: implement this behavior

import Shape.*

object boundingBox:
  def apply(s: Shape): Location = s match
    case Rectangle(w, h) => Location(0, 0, Rectangle(w, h))
    case Ellipse(w, h) => Location(0, 0, Rectangle(w, h))
    case Location(x, y, shape) => 
      val innerBox = apply(shape)
      Location(x + innerBox.x, y + innerBox.y, innerBox.shape)
    case Group(shapes*) => 
      if shapes.isEmpty then Location(0, 0, Rectangle(0, 0))
      else
        val boxes = shapes.map(apply)
        val minX = boxes.map(_.x).min
        val minY = boxes.map(_.y).min
        val maxX = boxes.map(b => b.x + b.shape.asInstanceOf[Rectangle].width).max
        val maxY = boxes.map(b => b.y + b.shape.asInstanceOf[Rectangle].height).max
        Location(minX, minY, Rectangle(maxX - minX, maxY - minY))

end boundingBox
