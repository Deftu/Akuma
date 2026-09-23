# This file

**No agent may edit `AGENTS.md` without first asking the user and being told it may.** Ask, wait
for an answer, then edit — a standing instruction elsewhere in the session does not count, and
neither does a change that looks obviously correct. Suggest the edit in your response instead and
let the user decide.

**This file holds rules and conventions only.** Nothing that describes the current state of the
tree belongs here — not what is built, not what is broken, not what has yet to be written. State
goes in `.agents/`; a rule is a thing that stays true after the state changes. That constraint
governs every future edit to this file, not just the ones made so far: if the sentence would need
rewriting once someone fixes something, it is not a rule.

# Response style

- Keep responses short and scannable — ADHD-friendly reading.
- Bullet points over paragraphs.
- Lead with the actionable part first.
- Skip preamble, meta-announcements ("Here is the code..."), and unnecessary explanation.
- Don't restate what the user already said.
- **Provide reasoning and concise examples over single-option mandates.** When asked open-ended choices (e.g., "What tech stack should I use?"), present the viable options with their trade-offs and examples. Do not pick one default winner for the user unless explicitly requested.
- Forbidden vocabulary: *delve, intricate, crucial, showcase, pivotal, tapestry, landscape, enhance, foster, underscore, vibrant, boasts, serves as*.

# `.agents/`

`.agents/` (gitignored, local only) is the project's local scratchpad, testing ground, progress tracker, and ongoing plan of record. `README.md` inside it indexes the contents.

- **Strict Size Limits:** Keep files in `.agents/` as concise as possible to conserve token windows while preserving high-signal context for human readers and future agents. Aim to keep primary state files like `NEXT-SESSION.md` under 100 lines.
- **Aggressive Truncation:** Overwrite or archive stale history rather than appending to long files. If a task is complete, summarize the outcome in 1–2 bullet points and drop the intermediate debugging steps.
- **Session Handoffs:** Use `.agents/NEXT-SESSION.md` as the primary workspace handoff. Update it continuously with direct, high-density bullets: current state, open blockers, and exact next steps.
- **Source is the truth once code exists:** Read design docs before writing code. When a doc disagrees with the code, fix the doc in the same change and note it under "Deviations" in `PROGRESS.md`. Keep it current as you work — a note that went stale silently is worse than no note.
- **No internal leakage:** Nothing in `.agents/` is user-facing documentation; the root `README.md` is. **Never point at `.agents/` from code, a comment, a commit message or a PR description** — it is context nobody reading the code has, and it is not committed. Restate the reasoning inline instead.

# Context & Instruction Verification

- **Instruction Integrity Check:** On every turn, silently verify that you are adhering to all active constraints in `AGENTS.md` and `CLAUDE.md`.
- **Context Drift Notification:** If the conversation history becomes long or dense, or if you detect that previous turns lost track of project rules, explicitly notify the user at the start of your response that context drift may be occurring and recommend running a context refresh (e.g., `/compact` or starting a fresh session). Re-read `AGENTS.md` before generating code.

# Ambiguity & Assumptions

- **Explicit Assumptions:** If a spec or task is ambiguous, state your assumptions explicitly to the user before writing code.
- **Ask When Destructive:** If an assumption involves breaking changes, public API redesigns, or database schema migrations, stop and ask the user for clarification first. Renaming a private helper is not a breaking change.

# Testing

- **Write the test first.** Red before green: a failing test naming the behaviour, then the code that satisfies it.
- A test that has never failed has not been shown to discriminate. Make it fail on purpose before trusting it.
- Bug fixes start with the test that reproduces the bug.
- Skip TTD for throwaway scripts, prototypes, and anything in `.agents/`; ask the user directly if it's unclear.

# Comments & Code Hygiene

- **Comment the trade-offs, not the code.** Write comments ONLY for non-obvious safety bounds, `SAFETY:` contracts, platform quirks, or unintuitive business logic.
- **Zero Narrative / Explainer Comments:** Never write top-of-file overview blocks, reverse-engineering summaries, or code-to-English line translations. Most functions need no comment at all.
- **No Line-by-Line Summaries:** Do not describe the change you just made or what the code "used to" do.
- **Never Point at Internal Context:** Never point at `.agents/` or any internal planning doc from code, inline comments, or commit messages. Restate reasoning inline instead.
- **No Defensive Over-Checking:** Do not add filler wrapper functions, defensive logging for impossible states, or unused abstractions.