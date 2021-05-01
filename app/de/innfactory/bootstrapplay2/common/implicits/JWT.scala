package de.innfactory.bootstrapplay2.common.implicits

import de.innfactory.auth.firebase.validator.JwtToken

object JWT {

  implicit class JwtTokenGenerator(authHeader: String) {
    def toJwtToken: JwtToken =
      authHeader match {
        case token: String if token.startsWith("Bearer") =>
          JwtToken(token.splitAt(7)._2)
        case token                                       => JwtToken(token)
      }
  }

}
