package com.mogproject.mogami.frontend.bench

import com.mogproject.mogami._
import com.mogproject.mogami.core.state.StateCache.Implicits.DefaultStateCache
import com.mogproject.mogami.frontend.{PlaygroundSAM, TestSettings}
import com.mogproject.mogami.frontend.action.UpdateGameControlAction
import com.mogproject.mogami.frontend.model.DeviceType.PC
import com.mogproject.mogami.frontend.model.board.BoardIndexNumber
import com.mogproject.mogami.frontend.model.{English, GameControl, PlayMode, PlaygroundConfiguration, PlaygroundModel}
import com.mogproject.mogami.frontend.state.{ObserveFlag, PlaygroundState}
import com.mogproject.mogami.frontend.view.TestView
import scalatags.JsDom.all.div

/**
  * Benchmark
  *
  * run `make clean && make bench` in the project root directory
  */
object BenchmarkJS extends TestData {
  TestSettings

  def main(args: Array[String]): Unit = {
    print("\n" * 6)

    setupSAM(recordUsen01)

    benchNavigateGame()
    benchNavigateGamePhase1()
    benchNavigateGamePhase2()
    benchNavigateGamePhase3()

    benchViewRefresh("benchRefreshMainPane", _.website.mainPane.refresh)
    benchViewRefresh("benchRefreshShareMenu", _.website.shareMenu.refresh)
    benchViewRefresh("benchRefreshResignButton", _.website.navBar.resignButton.refresh)
    benchViewRefresh("benchRefreshBranchArea", _.website.mainPane.sideBarLeft.get.branchArea.refresh)
    benchViewRefresh("benchRefreshPointCountButton", _.website.analyzeMenu.pointCountButton.refresh)
    benchViewRefresh("benchRefreshControlBar1", _.website.mainPane.sideBarLeft.get.controlBar.refresh)
    benchViewRefresh("benchRefreshControlBar2", _.website.mainPane.controlBar.refresh)
    benchViewRefresh("benchRefreshCommentArea", _.website.mainPane.commentArea.refresh)

  }

  //
  // Utilities
  //
  private[this] def withBenchmark(benchCount: Int)(thunk: => Unit): BenchResult = {
    val ret = (1 to benchCount).map { n =>
      val start = System.currentTimeMillis()
      thunk
      (System.currentTimeMillis() - start).toDouble
    }
    BenchResult(ret)
  }

  private[this] def runBench(label: String, loopCount: Int, warmUpCount: Int, benchCount: Int)(thunk: => Unit): Unit = {
    println("#" * 80 + s"\n#${label} (loop=${loopCount}, bench=${benchCount}):")

    // warm up
    (1 to warmUpCount).foreach(_ => (1 to loopCount).foreach { _ => thunk })

    // benchmark
    val result = withBenchmark(benchCount) {
      (1 to loopCount).foreach { _ => thunk }
    }

    // print result
    result.print()
  }


  private[this] def setupSAM(usen: String): Unit = {
    val mode = PlayMode(GameControl(Game.parseUsenString(usen)), None)
    val config = PlaygroundConfiguration(
      boardIndexType = BoardIndexNumber,
      messageLang = English,
      recordLang = English,
      baseUrl = "",
      deviceType = PC,
      visualEffectEnabled = false
    )
    val model = PlaygroundModel(mode, config)
    val view = TestView(false, false, false, false, false, div().render)
    val state = PlaygroundState(model, view)

    PlaygroundSAM.initialize(state)
  }

  //
  // Benchmark Scenarios
  //
  private[this] def benchNavigateGame(): Unit = {
    def f(): Unit = {
      PlaygroundSAM.doAction(UpdateGameControlAction(_.withFirstDisplayPosition))
      (1 to 163).foreach { _ => PlaygroundSAM.doAction(UpdateGameControlAction(_.withNextDisplayPosition)) }
    }

    runBench("benchNavigateGame", 10, 3, 5)(f())
  }

  private[this] def benchNavigateGamePhase1(): Unit = {
    val mode = PlayMode(GameControl(Game.parseUsenString(recordUsen01)), None)
    val config = PlaygroundConfiguration(visualEffectEnabled = false)
    val model = PlaygroundModel(mode, config)

    runBench("benchNavigateGamePhase1", 10, 3, 5) {
      UpdateGameControlAction(_.withFirstDisplayPosition).execute(model)
      (1 to 163) foreach { _ =>
        UpdateGameControlAction(_.withNextDisplayPosition).execute(model)
      }
    }
  }

  private[this] def benchNavigateGamePhase2(): Unit = {
    val mode = PlayMode(GameControl(Game.parseUsenString(recordUsen01)), None)
    val config = PlaygroundConfiguration(visualEffectEnabled = false)
    val model = PlaygroundModel(mode, config)
    val view = TestView(false, false, false, false, false, div().render)
    val initState: PlaygroundState = PlaygroundState(model, view)
    var state: PlaygroundState = PlaygroundState(model, view)

    runBench("benchNavigateGamePhase2", 10, 3, 5) {
      state = initState
      (1 to 163) foreach { _ =>
        val nextModel = UpdateGameControlAction(_.withNextDisplayPosition).execute(state.model).get
        state.getObserveFlag(nextModel)
        // flag = 1<<21 + 1<<22 + 1<<24
        state = state.copy(model = nextModel)
      }
    }
  }

  private[this] def benchNavigateGamePhase3(): Unit = {
    val mode = PlayMode(GameControl(Game.parseUsenString(recordUsen01)), None)
    val config = PlaygroundConfiguration(visualEffectEnabled = false)
    val model = PlaygroundModel(mode, config)
    val view = TestView(false, false, false, false, false, div().render)
    val initState: PlaygroundState = PlaygroundState(model, view)
    var state: PlaygroundState = PlaygroundState(model, view)

    runBench("benchNavigateGamePhase3", 10, 3, 5) {
      state = initState
      (1 to 163) foreach { _ =>
        val nextModel = UpdateGameControlAction(_.withNextDisplayPosition).execute(state.model).get
        state.getObserveFlag(nextModel)
        state.render(nextModel)
        state = state.copy(model = nextModel)
      }
    }
  }

  private[this] def benchViewRefresh(label: String, refreshFunc: TestView => (PlaygroundModel, Long) => Unit): Unit = {
    val mode = PlayMode(GameControl(Game.parseUsenString(recordUsen01)), None)
    val config = PlaygroundConfiguration(visualEffectEnabled = false)
    val model = PlaygroundModel(mode, config)
    val view = TestView(false, false, false, false, false, div().render)
    val models = (1 to 163).scanLeft(model) { case (m, _) =>
      UpdateGameControlAction(_.withNextDisplayPosition).execute(m).get
    }
    import ObserveFlag._
    val flag = GAME_POSITION | GAME_INDICATOR | GAME_JUST_MOVED

    runBench(label, 10, 3, 5) {
      models.foreach(m => refreshFunc(view)(m, flag))
    }
  }

}