package de.innfactory.bootstrapplay2.commons.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.{FirebaseApp, FirebaseOptions}

object FirebaseBase {

  def instantiateFirebase(serviceAccountJsonFilepath: String, projectId: String = null): FirebaseApp = {
    val serviceAccount = getClass.getClassLoader.getResourceAsStream(serviceAccountJsonFilepath)

    val options: FirebaseOptions = FirebaseOptions
      .builder()
      .setCredentials(GoogleCredentials.fromStream(serviceAccount))
      .setProjectId(projectId)
      .build

    FirebaseApp.initializeApp(options)
  }

  def deleteFirebase(): Unit =
    FirebaseApp.getInstance().delete()

}
