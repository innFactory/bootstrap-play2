package packagedomain.domainfiles.common

trait CrudHelper {
  protected val CrudLogicKey = "%%CRUD_LOGIC%%"
  protected val CrudImportsKey = "%%CRUD_IMPORTS%%"

  protected def replaceForCrud(content: String, withCrud: Boolean, crudLogic: String, crudImports: String): String =
    content
      .replaceAll(CrudLogicKey, if (withCrud) crudLogic else "")
      .replaceAll(CrudImportsKey, if (withCrud) crudImports else "")
}
