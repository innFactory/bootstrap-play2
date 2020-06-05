package common.GeoPointFactory

import com.vividsolutions.jts.geom.{ Coordinate, GeometryFactory }

object GeoPointFactory {
  val factory = new GeometryFactory()

  /**
   * Creates GeoPoint for given lon and lat values
   * @param lon
   * @param lat
   * @return
   */
  def createPoint(lon: Double, lat: Double) = {
    val point = factory.createPoint(new Coordinate(lon, lat))
    factory.createGeometry(point)
  }
}
