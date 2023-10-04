package common

object SeqImplicit {
  implicit class EmptySeqOption[A](seq: Seq[A]) {
    def toOption: Option[Seq[A]] = Option(seq).filter(_.nonEmpty)
  }
}
