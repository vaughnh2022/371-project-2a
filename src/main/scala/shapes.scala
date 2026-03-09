package edu.luc.cs.laufer.cs371.shapes

/** data Shape = Rectangle(w, h) | Ellipse(w, h) | Location(x, y, Shape) | Group(shapes*) */
enum Shape derives CanEqual:
  case Rectangle(val width: Int, val height: Int)
  case Ellipse(val width: Int, val height: Int)
  case Location(val x: Int, val y: Int, val shape: Shape)
  case Group(val shapes: Shape*)

extension (s: Shape)
  def height: Int = s match
    case Shape.Rectangle(_, h) => h
    case Shape.Ellipse(_, h) => h
    case _ => throw new UnsupportedOperationException(s"height is not defined for $s")

extension (s: Shape)
  def width: Int = s match
    case Shape.Rectangle(w, _) => w
    case Shape.Ellipse(w, _) => w
    case _ => throw new UnsupportedOperationException(s"width is not defined for $s")

extension (s: Shape)
  def x: Int = s match
    case Shape.Location(x, _, _) => x
    case _ => throw new UnsupportedOperationException(s"x is not defined for $s")

extension (s: Shape)
  def y: Int = s match
    case Shape.Location(_, y, _) => y
    case _ => throw new UnsupportedOperationException(s"y is not defined for $s")

extension (s: Shape)
  def shape: Shape = s match
    case Shape.Location(_, _, shape) => shape
    case _ => throw new UnsupportedOperationException(s"shape is not defined for $s")

extension (s: Shape)
  def shapes: Seq[Shape] = s match
    case Shape.Group(shapes*) => shapes
    case _ => throw new UnsupportedOperationException(s"shapes is not defined for $s")
