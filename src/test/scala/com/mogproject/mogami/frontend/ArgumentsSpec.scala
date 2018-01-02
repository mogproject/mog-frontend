package com.mogproject.mogami.frontend

import com.mogproject.mogami.core.game.Game
import com.mogproject.mogami.core.state.StateCache
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, MustMatchers}

class ArgumentsSpec extends FlatSpec with MustMatchers with GeneratorDrivenPropertyChecks {
  "Arguments#parseQueryString" must "create configuration" in {
    Arguments().parseQueryString("") mustBe Arguments()
    Arguments().parseQueryString("?mlang=en") mustBe Arguments(config = BasePlaygroundConfiguration(messageLang = English))
    Arguments().parseQueryString("?sfen=lnsgkgsnl%2F1r5b1%2Fppppppppp%2F9%2F9%2F9%2FPPPPPPPPP%2F1B5R1%2FLNSGKGSNL%20b%20-%200%207g7f&mlang=en") mustBe Arguments(
      sfen = Some("lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b - 0 7g7f"),
      config = BasePlaygroundConfiguration(messageLang = English)
    )
  }
}

class ArgumentsBuilderSpec extends FlatSpec with MustMatchers with GeneratorDrivenPropertyChecks  {
  "ArgumentsBuilder#toRecordUrl" must "include comments" in StateCache.withCache { implicit cache =>
    val g = Game.parseUsenString("~0.6y20io5t21im.~0.7ku2jm6fu3om.~0.7ku2jm7ga2o6.")
    val comments = Map(
      g.trunk.historyHash(0) -> "c-0.0",
      g.trunk.historyHash(1) -> "c-0.1",
      g.trunk.historyHash(2) -> "c-0.2",
      g.trunk.historyHash(3) -> "c-0.3",
      g.trunk.historyHash(4) -> "c-0.4",
      g.branches(0).historyHash(1) -> "c-1.1",
      g.branches(0).historyHash(2) -> "c-1.2",
      g.branches(0).historyHash(3) -> "c-1.3",
      g.branches(0).historyHash(4) -> "c-1.4",
      g.branches(1).historyHash(3) -> "c-2.3",
      g.branches(1).historyHash(4) -> "c-2.4"
    )
    val expected = "http://localhost/?u=~0.6y20io5t21im.~0.7ku2jm6fu3om.~0.7ku2jm7ga2o6.&c0=c-0.0&c1=c-0.1&c2=c-0.2&c3=c-0.3&c4=c-0.4&c1.1=c-1.1&c1.2=c-1.2&c1.3=c-1.3&c1.4=c-1.4&c2.3=c-2.3&c2.4=c-2.4"
    ArgumentsBuilder(GameControl(g.copy(newComments = comments))).toRecordUrl mustBe expected
  }

}