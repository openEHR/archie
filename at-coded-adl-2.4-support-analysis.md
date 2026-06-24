# At-coded ADL 2.4 support in Archie — remaining-work analysis

Analysis of what remains to fully support ADL 2.4 **at-coded** archetypes in Archie,
beyond the archetype-validator work already done on branch `791_validate_2.4_at_coded_adl`.

## Background

In normal ADL 2 **id-coded** archetypes, structural node identifiers use the `id` prefix
(`id1`, `id1.1`), value codes use `at` (`at0001`), and value-set codes use `ac` (`ac0001`).
In ADL 2.4 **at-coded** archetypes, structural node identifiers *also* use the `at` prefix
(e.g. `CLUSTER[at0000]`, `ELEMENT[at0001]`).

Two consequences drive every finding below:

1. **Prefix ambiguity** — node-ids and value codes both use `at`, so a code can only be
   classified as node-id-vs-value by structural context (a `CObject.getNodeId()` vs a code
   inside `CTerminologyCode`/`ValueSet`/terminology), never by prefix alone.
2. **Zero-padding** — at-coded node-ids retained from ADL 1.4 are zero-padded (`at0000`,
   `at0000.1`). The strict ADL 2 code regex
   (`AOMUtils.isValidCode`, `(id|at|ac)(0|[1-9][0-9]*)...`) **rejects** these; the lenient
   `AOMUtils.isValidADL14Code` (`(id|at|ac)([0-9]+)...`) accepts them.
   Note: codes *generated* by the converter (value-set `ac2`/`ac9000`, synthetic node-ids in
   the `9000` range) are **not** zero-padded, so they pass the strict regex.

The validator PR already switched `AOMUtils.codesConformant` and
`AOMUtils.pathAtSpecializationLevel` to the lenient check; the remaining gaps are other
helpers that still assume strict/id-coded forms.

## What already works (verified empirically)

Verified by running an OPT-generation + ADL serialization round-trip probe and reading the
implementations:

- Parse → validate (single **and** specialized) → flatten → **OPT generation** → **ADL
  serialize + re-parse** round-trips cleanly.
  - OPT root node id `at0000`, root term resolves (`"Demonstration"`), 99 term definitions present.
  - Serialized ADL contains `at0000`, does **not** leak `id9999`, re-parses with no errors,
    root stays `at0000`.
- Path / AQL resolution: `PathSegment`'s node-id regex accepts `at` *and* zero-padding, so
  paths like `/items[at0001]/value` resolve.
- RM instance generation and rules evaluation classify value codes by the `at` prefix — fine.

### Overstated claims that were debunked

- **"`id9999` contaminates serialization"** — the `id9999` primitive sentinel does **not** leak
  into serialized ADL and does not break round-trip; it is internal only.
- **"OPT terminology is missing/empty for at-coded"** — top-level OPT terminology is complete
  (99 term defs, root term resolves). (`TerminologyContentGenerator` is a separate authoring
  tool, not OPT generation — see #4 below.)
- **"`isValidValueSetCode` breaks `ac` codes"** — real at-coded `ac` codes are not zero-padded
  (`ac2`, `ac9000`), so they pass; only a hypothetical `ac0000` would fail.

## Remaining gaps

Common thread: a handful of `AOMUtils` helpers still use strict `isValidCode` (or
`parseInt > 0`, or `isIdCode`) where they should tolerate zero-padded at-codes — the same fix
already applied to `codesConformant` / `pathAtSpecializationLevel`.

| # | Gap | Where | Impact | Severity |
|---|-----|-------|--------|----------|
| 1 | `codeExistsAtLevel` does `Integer.parseInt("0000") > 0` → **false** for the `at0000` root | `AOMUtils:~205` → feeds `getSpecialisationStatusFromCode` → **archetype diff** (`NodeIdLCS`, `LCSOrderingDiff`) & value-set redefinition checks | Differential **diff** of at-coded archetypes likely wrong (untested) | **Medium** |
| 2 | `isPhantomPathAtLevel` uses strict `isValidCode` → skips zero-padded at-code path segments | `AOMUtils:189` (specialization validation) | Latent: phantom-path detection skipped for some specialized at-coded archetypes (the specialized-flatten test passes, so impact is narrow) | Low–Medium |
| 3 | `OperationalTemplate.getTerm` gates `stripLastPartOfPath` on `isIdCode(code)` for `CArchetypeRoot` | `OperationalTemplate:182` | Term lookup for **slot-filled nested roots in at-coded *templates*** (top-level OPT is fine) | Medium (templates) |
| 4 | `TerminologyContentGenerator` uses `hasIdCodeInAllLanguages` + an `id[0-9]+` comment regex | `adlchecker` module | **Terminology auto-generation** ("add term for me", comment extraction) broken for at-coded | Medium (authoring tool) |
| 5 | Prefix cannot distinguish node-id from value code: `idCodes()`, `hasIdCode` / `hasIdCodeInAllLanguages`, `getUsedIdCodes()` / `getUsedValueCodes()` | `ArchetypeTerminology`, `Archetype` | **No core callers today** (the one in `CodeValidation` was already fixed); latent landmine for future code | Low (latent) |
| 6 | `stripPrefix` uses strict `isValidCode` → leaves `at0000` unstripped | `AOMUtils:65` | Only the ADL 1.4-compatibility duplicate-id *warning* | Cosmetic |
| 7 | `ArchetypeParsePostProcessor:37` stamps every primitive object with `id9999` regardless of code system | model post-processing | Verified it does not leak to ADL / round-trip; but in-memory an at-coded archetype contains one id-code | Cosmetic / conceptual |
| 8 | No at-coded cases in `ADLArchetypeSerializerParserRoundtripTest`; no OPT test for at-coded | tests | Works today but unguarded against regression | Low (test debt) |

## Recommended order

None of these block the validator PR's stated goal — validating at-coded archetypes works
end to end including flatten, OPT, and round-trip. They matter for *full* support:

1. **Diff support (#1)** — most likely to silently produce wrong results; one focused fix to
   `codeExistsAtLevel` plus a diff test.
2. **Strict-`isValidCode` sweep (#2, #6)** — small, consistent, low-risk (switch to
   `isValidADL14Code`, as already done elsewhere).
3. **At-coded templates (#3)** and **terminology auto-generation (#4)** — only if those
   features are in scope for at-coded.
4. **Regression tests (#8)** — cheap insurance for the round-trip / OPT paths verified by hand.

## Test-first verification of the gaps

To decide which gaps actually need fixing — rather than acting on the (largely *untested* /
*latent*) severities above — a probe test was written for each gap. The principle: encode the
**desired** behaviour, then run it. A failing test means the gap is real and the fix is
necessary; a passing test means the gap does not manifest (and the test stays as a regression
guard).

**On "latent" gaps and test-first.** A latent gap (no caller reaches the broken path with
at-coded input) cannot be driven by a failing behavioural test — there is no observable wrong
behaviour. For those (#2, #5, #7) the probe can only (a) pin current behaviour as a regression
guard, or (b) document the mechanism a fix would change. A green latent probe does **not** prove
the fix is unnecessary, and a red unit probe does **not** prove it is necessary; necessity is a
scope decision about whether a real caller will ever appear.

| # | Test | Module / class | Result | Conclusion |
|---|------|----------------|--------|------------|
| 1 (unit) | `specialisationStatusOfAtCodedRoot` | `aom` · `AOMUtilsTest` | 🔴 fails | Real at unit level: `codeExistsAtLevel("at0000.1", 0)` is `false` and `at0000.1` classifies as `ADDED` not `REDEFINED` (id twin: `REDEFINED`). |
| 1 (e2e) | `atCodedSpecializationRoundTrips` | `tools` · `AtCodedDiffTest` | 🟢 passes | The real at-coded parent→child differential diff round-trips byte-for-byte. The unit bug does **not** manifest on this fixture, so #1's "Medium" severity is **downgraded**. |
| 2 | `phantomPathGuardRejectsZeroPaddedAtCode` | `aom` · `AOMUtilsTest` | 🟢 passes (characterization) | Latent. Pins the mechanism (`isValidCode` rejects, `isValidADL14Code` accepts `at0001.1`); the helper returns `false` regardless, so the proposed fix is a **behavioural no-op** today. |
| 3 | `atCodedTermForUseArchetype` | `tools` · `ArchetypeTerminologyTest` | 🔴 fails | **Real and behavioural.** `getTerm()` on an at-coded slot-filled root returns the *included* archetype's own root term (`"the generic entry"`) instead of the parent's use-node term (`"included archetype en"`, as the id-coded twin returns). In scope → fix necessary. |
| 4 (root cause) | `atCodedNodeIdsRecognisedByTerminology` | `tools` · `ArchetypeTerminologyTest` | 🔴 fails | Real: `hasIdCodeInAllLanguages("at0000")` is `false` though the term is present, and `idCodes()` cannot enumerate at-coded node ids. NB: the generator itself (`TerminologyContentGenerator`) lives in the `adlchecker` module, which is **not in `settings.gradle`** and compiles against published `archie-all` — it is neither built nor testable in CI; this probe targets the buildable root-cause helper. |
| 5 | `usedCodeClassificationConflatesAtCodedNodeIdsAndValues` | `tools` · `AtCodedLatentBehaviourTest` | 🟢 passes (characterization) | Latent. `getUsedIdCodes()` is empty for at-coded; the `at0000` node id is misclassified into `getUsedValueCodes()`. |
| 6 | `stripPrefix` | `aom` · `AOMUtilsTest` | 🔴 fails | Real (cosmetic-impact): `stripPrefix("at0000")` returns `"at0000"` unchanged instead of `"0000"`. |
| 7 | `primitivesCarryIdCodedSentinelButItDoesNotLeak` | `tools` · `AtCodedLatentBehaviourTest` | 🟢 passes (characterization) | Latent/cosmetic. A primitive's node id is `id9999` in memory, but it is confirmed **not** to leak into serialized ADL. |

New fixtures added for #3: `openEHR-EHR-COMPOSITION.atparent.v1.0.0.adls` and
`openEHR-EHR-GENERIC_ENTRY.atincluded.v1.0.0.adls` (at-coded analogues of the existing id-coded
`parent`/`included` use-archetype pair). #1/#4/#5/#7 reuse the existing at-coded fixtures under
`/com/nedap/archie/adl14/`.

The 🔴 tests are intentionally red — they encode desired behaviour and form the worklist; the 🟢
characterization tests are guards. The probes used a warning-tolerant parser (asserting
`hasNoErrors`) rather than the shared `FlattenerTestUtil.parse`, which rejects the FULL AMBIGUITY
*warnings* the CKM-derived at-coded fixtures emit.

### What the verification changes

- **Confirmed real, fix-worthy:** #3 (templates, in scope), #4 (terminology root cause), #6 (trivial).
- **Real but lower priority than stated:** #1 — unit-level bug confirmed, but end-to-end diff round-trips, so not the "Medium" risk originally feared.
- **Latent — necessity is a scope decision, not a test outcome:** #2 (and likely a no-op fix), #5, #7. Now pinned as regression guards.
