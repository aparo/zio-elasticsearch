// Copyright: 2018 https://www.megl.io
// License: http://www.megl.io/licenses/LICENSE-2.0

import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._

object Licensing {

  private[this] val LicenseYear = "2019"

  private[this] val licenseText =
    """Alberto Paro"""

  val settings = Seq(
    headerLicense := Some(HeaderLicense.ALv2(LicenseYear, licenseText))
  )

}
