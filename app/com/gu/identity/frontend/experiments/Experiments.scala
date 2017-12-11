package com.gu.identity.frontend.experiments

object ActiveExperiments {
  val allExperiments: Set[Experiment] = Set(
    StopConsentCollection
  )
}

object StopConsentCollection extends Experiment(
  name = "stop-consent-collection",
  description = "Users in this experiment won't get asked for V1 consents at sign up.",
  defaultStatus = false
)

