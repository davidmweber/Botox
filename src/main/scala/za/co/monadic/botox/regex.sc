import scala.util.matching.Regex

implicit class RegexContext(sc: StringContext) {
  def r = new Regex(sc.parts.mkString, sc.parts.tail.map( _ => "x") : _*)
}

"123" match { case r"(\d+)$d" => d.toInt case _ => 0 }

object Doubler { def unapply(s: String) = Some(s.toInt*2) }

"10" match { case r"(\d\d)${Doubler(d)}" => d case _ => 0 }

"foo 10" match { case r"foo (\d\d)${Doubler(d)}" => d case _ => 0 }
