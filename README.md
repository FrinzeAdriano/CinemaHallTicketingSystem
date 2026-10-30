# Cinema Ticket Management System

A console-based (CUI) cinema ticketing system built with an MVC architecture. It supports walk-in sales (cashier) and online booking (customer), with an admin role for managing movies, rooms, schedules, prices, users and inventory.

---

# Team Guide: Using GitHub with NetBeans

## One-time setup (each person does this)

**1. Get a GitHub account and be added to the repo**
- Send your GitHub username to the repo owner.
- Accept the collaborator invitation (check your email or github.com/notifications).

**2. Generate your own token** (never share or reuse someone else's)
- GitHub: **Settings > Developer settings > Personal access tokens**
- Create a token with access to this repo (a classic token with the `repo` scope is the simplest).
- Set an expiration (30 to 90 days) and copy it right away. GitHub shows it only once.

**3. Set your Git identity in NetBeans**
- **Team > Git > Set User Name and Email** (or Options > Team > Git).
- Use your own name and email so commits are credited to you.

**4. Clone the repo**
- **Team > Git > Clone**
- Paste the repo URL.
- Username: your GitHub username. Password: **your token**.
- Select the `main` branch, finish, then open the project.
- Do **not** create a new NetBeans Git repository for the project. Always clone.

**5. Create your own branch**
- Switch to `main`, then **Team > Remote > Pull**.
- **Team > Branch/Tag > Create Branch**, name it (for example `yourname-branch`), and tick **Checkout created branch**.

---

## Everyday workflow

1. **Update first.** **Team > Remote > Fetch**, then **Team > Git > Merge Revision** and pick `origin/main` to bring the latest team work into your branch.
2. **Check your branch.** The branch name shows beside the project in the Projects tab. It must be **your branch**, never `main`.
3. **Work, then save your files.**
4. **Commit (local only).** **Team > Commit**, untick junk files, write a clear message (for example "Add seat booking to Schedule"), and click **Commit**.
5. **Push (uploads to GitHub).** **Team > Remote > Push**, choose `origin`, select your branch, finish.
6. **Open a pull request (on GitHub).**
   - Click **Compare & pull request**.
   - Base: `main`. Compare: your branch.
   - Add a short description and click **Create pull request**.
   - Tell the team in the group chat.
7. **Merge.** After the team agrees, click **Merge pull request**.
8. **Refresh your local copy.** Switch to `main`, **Team > Remote > Pull**, then switch back to your branch and merge `origin/main` into it.

---

## Rules to avoid problems

- **Never commit directly to `main`.** Always use your own branch and a pull request.
- **Never push `build/`, `dist/` or `nbproject/private/`.** Keep them in `.gitignore`.
- **Pull before you start working**, and again before you push.
- **Commit small and often** with clear messages.
- **Merge pull requests quickly.** Old branches drift and cause conflicts.
- **Divide classes by person.** Tell the group before editing a shared class like `Transaction` or `Schedule`.
- **Don't reformat or reorder whole files.**
- **Never put your token in code, commit it, or send it in chat.** If it leaks, revoke it on GitHub and make a new one.
- **Compile and run before pushing** so you don't break `main` for everyone.

---

## If there's a merge conflict

1. Merge `origin/main` into your branch (**Team > Git > Merge Revision**).
2. **Team > Git > Resolve Conflicts** and open each conflicted file.
3. Look for the markers:
   ```
   <<<<<<< HEAD
   your version
   =======
   their version
   >>>>>>> origin/main
   ```
4. Keep what's needed (usually both), delete the three marker lines, and save.
5. Compile and test, then **Team > Commit** and **Team > Remote > Push**.

If you're unsure, ask the group before choosing which version to delete.

---

## Quick troubleshooting

| Problem | Fix |
|---|---|
| Login fails on push or clone | Use your **token** as the password, not your GitHub password |
| Push rejected | Fetch and merge `origin/main` into your branch first, then push again |
| Can't switch branches | Commit your changes first |
| Committed to `main` by mistake | Tell the team before pushing |
| Commit window shows no files | Save your files, or the changes were already committed |
| Don't see the Compare & pull request banner | Open the **Pull requests** tab and click **New pull request** |

> Menu names vary slightly between NetBeans versions. If you can't find an item under **Team**, right-click the project and look under **Git**.
