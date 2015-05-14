package com.musicPlayer.JML;

import com.sun.javafx.Utils;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author dean
 */
public class SpectrumBar extends VBox {
  private static final double SPACING = 1.0;
  private static final double ASPECT_RATIO = 3;
  private static final double MIN_BAR_HEIGHT = 3;
  private static final double SPACING_RATIO = 0.1;
  
  private final int maxValue;
  private final int barCount;

  private double lastWidth = 0;
  private double lastHeight = 0;
  
  public SpectrumBar(int maxValue, int barCount) {
    this.maxValue = maxValue;
    this.barCount = barCount;
    
    getStyleClass().add("spectrumBar");
    setSpacing(SPACING);
    setAlignment(Pos.BOTTOM_CENTER);
    
    Stop[] stops = new Stop[3];
    stops[0] = new Stop(0.3, Color.RED);
    stops[1] = new Stop(0.7, Color.YELLOW);
    stops[2] = new Stop(0.9, Color.web("56F32B"));
    
    for (int i = 0; i < barCount; i++) {
      int c = (int)((double)i / (double)barCount * 255.0);
      final Rectangle r = new Rectangle();
      r.setVisible(false);
      r.setFill(Utils.ladder(Color.rgb(c, c, c), stops));
      r.setArcWidth(2);
      r.setArcHeight(2);
      getChildren().add(r);
    }
  }
  
  public void setValue(double value) {
    int barsLit = Math.min(barCount, (int)Math.round(value/maxValue*barCount));
    ObservableList<Node> childList = getChildren();
    for (int i = 0; i < childList.size(); i++) {
      childList.get(i).setVisible(i > barCount - barsLit);
    }
  }
  
  @Override
  protected double computeMinHeight(double width) {
    return computeHeight(MIN_BAR_HEIGHT);
  }

  @Override
  protected double computeMinWidth(double height) {
    return computeWidthForHeight(MIN_BAR_HEIGHT);
  }

  @Override
  protected double computePrefHeight(double width) {
    return computeHeight(5);
  }

  @Override
  protected double computePrefWidth(double height) {
    return computeWidthForHeight(5);
  }

  @Override
  protected void layoutChildren() {
    if (lastWidth != getWidth() || lastHeight != getHeight()) {
      double spacing = SPACING * (barCount-1);
      double barHeight = (getHeight() - getVerticalPadding() - spacing) / barCount;
      double barWidth = Math.min(barHeight * ASPECT_RATIO, 
                                 getWidth()-getHorizontalPadding());

      for (Node node : getChildren()) {
        Rectangle r = (Rectangle) node;
        r.setWidth(barWidth);
        r.setHeight(barHeight);
      }
      
      lastWidth = getWidth();
      lastHeight = getHeight();
    }
    
    super.layoutChildren();
  }
  
  private double computeWidthForHeight(double barHeight) {
    double hpadding = getHorizontalPadding();
    return barHeight * ASPECT_RATIO + hpadding;
  }
  
  private double computeHeight(double barHeight) {
    double vpadding = getVerticalPadding();
    
    double barHeights = barHeight * barCount;
    double spacing = SPACING * (barCount-1);
    
    return barHeights + spacing + vpadding;
  }

  private double getVerticalPadding() {
    final Insets padding = getPadding();
    return padding.getTop() + padding.getBottom();
  }

  private double getHorizontalPadding() {
    final Insets padding = getPadding();
    return padding.getLeft() + padding.getRight();
  }
}