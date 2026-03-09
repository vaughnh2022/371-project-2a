# COMP 371 Project 2a - Shapes OO Scala

A visual Scala application for creating, manipulating, and analyzing geometric shapes with a Swing-based GUI.

## Project Overview

This project implements object-oriented shape abstractions in Scala using algebraic data types (enums). It includes:
- **Shape Definitions**: Rectangle, Ellipse, Location, and Group types
- **Bounding Box Calculations**: Automatic computation of minimal bounding rectangles
- **Interactive GUI**: Visual shape manipulation and creation
- **Unit Tests**: Comprehensive test coverage for all shape operations

## GUI Features

### Main Window
- **800x600 pixel canvas** with reference grid (50px spacing)
- **Sample shapes** pre-loaded on startup for demonstration
- **Real-time rendering** of all shapes with bounding boxes (drawn in red)

### Shape Types
- **Rectangles**: Displayed in blue with black outline
- **Ellipses**: Displayed in green with black outline
- **Located Shapes**: Positioned offset containers for other shapes
- **Groups**: Collections of multiple shapes rendered together

## Controls & Instructions

### Drag Mode (Default - Press **A**)
Move existing shapes around the canvas:
1. **Click and drag** any shape to reposition it
2. Shapes stay **within window boundaries**
3. **Collision detection** prevents shapes from overlapping
4. **Bounding boxes** help visualize the actual drawable area

### Draw Rectangle Mode (Press **D**)
Create new rectangular shapes:
1. Press **D** to enter Draw Rectangle mode
2. **Click and drag** on the canvas to define the rectangle
3. Drag from any corner - dimensions are automatically calculated
4. Rectangle must be **at least 5×5 pixels** to be created
5. Shape appears **instantly** on the canvas
6. Press **A** to return to drag mode

### Draw Ellipse Mode (Press **E**)
Create new elliptical shapes:
1. Press **E** to enter Draw Ellipse mode
2. **Click and drag** on the canvas to define the ellipse
3. Drag from any corner - dimensions are automatically calculated
4. Ellipse must be **at least 5×5 pixels** to be created
5. Shape appears **instantly** on the canvas
6. Press **A** to return to drag mode

### Mode Indicator
The **bottom-left corner** displays your current mode and available hotkeys for quick reference.

## Extra Credit Features

### Shape Dragging & Collision Detection
This project includes **extra credit work** implementing advanced GUI features:
- ✅ **Drag existing shapes** around the canvas with mouse support
- ✅ **Boundary checking** keeps shapes within the window at all times
- ✅ **Collision detection** prevents shapes from overlapping each other
- ✅ **Visual feedback** with bounding box highlighting during interaction

### Drawing New Shapes
Additional enhancements beyond the base requirements:
- ✅ **Draw mode toggle** for creating rectangles and ellipses dynamically
- ✅ **Live preview** shows shape outlines as you drag (semi-transparent)
- ✅ **Minimum size enforcement** (5×5 pixels) to prevent accidental tiny shapes
- ✅ **Multi-shape capacity** - draw as many shapes as desired

## Building & Running

### Prerequisites
- Java 17 or higher
- sbt (Scala Build Tool)

### Compile
```bash
sbt compile
```

### Run the GUI Application
```bash
sbt run
```

### Run Tests
```bash
sbt test
```

## Architecture

### Main Components

#### `Shape` Enum (shapes.scala)
Algebraic data type defining shape variants with value accessors via extension methods:
- `Rectangle(width: Int, height: Int)` - Rectangular shapes
- `Ellipse(width: Int, height: Int)` - Elliptical shapes
- `Location(x: Int, y: Int, shape: Shape)` - Positioned shape containers
- `Group(shapes: Shape*)` - Collections of shapes

#### `boundingBox` Object (boundingBox.scala)
Computes minimal bounding rectangles for all shape types, handling:
- Simple shapes (Rectangle, Ellipse)
- Positioned shapes with offset calculations
- Groups with recursive bounding box merging

#### `ShapePanel` Class (main.scala)
Interactive GUI panel with:
- Three operation modes (Drag, DrawRect, DrawEllipse)
- Mouse event handling for shape selection and drawing
- Keyboard event handling for mode switching
- Collision detection and boundary enforcement
- Real-time shape rendering

#### `TestDraw` (TestDraw.scala)
Comprehensive unit tests for drawing behavior using:
- `BufferedImage` for offline rendering verification
- Mock event simulation without GUI interaction
- 9 test cases covering all draw functionality

## File Structure
```
src/
  main/scala/
    shapes.scala       - Shape enum definitions and extension methods
    boundingBox.scala  - Bounding box calculation logic
    main.scala         - GUI implementation and event handling
  test/scala/
    TestDraw.scala     - Draw behavior unit tests
    TestHeight.scala   - Height property tests
    TestSize.scala     - Size property tests
    TestBoundingBox.scala - Bounding box calculation tests
    TestScale.scala    - Shape scaling tests
    TestFixtures.scala - Test data fixtures
    Main.scala         - Test runner
```

## Test Suite

- **29 total tests** ensuring correctness and robustness
- **Unit tests** for shape properties, bounding boxes, and drawing behavior
- **Integration tests** for GUI interactions using mock events
- **Offline rendering tests** using BufferedImage for deterministic verification

## Known Limitations

- GUI uses simple integer coordinates (floating-point not supported)
- Shapes are stored in 2D list buffer (no spatial indexing for large numbers of shapes)
- Collision detection uses bounding box approximation (accurate for rectangles, approximate for circles)
- Drawing preview is semi-transparent but doesn't support other visual effects
