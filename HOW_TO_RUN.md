# How to Run — Attack on Titan: Utopia

This project is a JavaFX desktop application. The entry point is **`game.gui.GameApp`**.

---

## 1. Prerequisites

| Requirement | Version |
|-------------|---------|
| **JDK**     | Java 11 or newer (Java 17 LTS recommended) |
| **JavaFX SDK** | 17 or newer — download from <https://gluonhq.com/products/javafx/> |
| **OS**      | macOS, Windows, or Linux |

> ⚠️ JavaFX is **not** bundled with the JDK starting from Java 11. You must download the JavaFX SDK separately and point the JVM at its `lib` folder using `--module-path` and `--add-modules`.

After downloading and unzipping JavaFX, note the absolute path to its `lib` directory. For example:
- macOS / Linux: `/Users/<you>/javafx-sdk-17/lib`
- Windows: `C:\javafx-sdk-17\lib`

We'll refer to that path below as `PATH_TO_FX`.

---

## 2. Required Working Directory

The game loads `titans.csv` and `weapons.csv` from the **current working directory** at startup. Always launch the game from the project root (the folder containing this file), otherwise `DataLoader` will throw an `IOException`.

---

## 3. Option A — Run from an IDE (Recommended)

### IntelliJ IDEA
1. Open the project folder in IntelliJ. It will detect the existing `.iml` file.
2. Make sure the **Project SDK** is set to JDK 11+ (`File → Project Structure → Project`).
3. Add the JavaFX library:
   - `File → Project Structure → Libraries → +` → **Java**
   - Select your `PATH_TO_FX` folder, click OK.
4. Add VM options for the run configuration:
   - `Run → Edit Configurations → + → Application`
   - **Main class:** `game.gui.GameApp`
   - **Working directory:** the project root
   - **VM options:**
     ```
     --module-path "PATH_TO_FX" --add-modules javafx.controls,javafx.fxml
     ```
5. Click **Run ▶**.

### Eclipse
1. `File → Import → Existing Projects into Workspace` and pick the project folder.
2. Right-click the project → `Build Path → Configure Build Path → Libraries → Add External JARs` and select every `.jar` inside `PATH_TO_FX`.
3. `Run → Run Configurations → Java Application → New`:
   - **Main class:** `game.gui.GameApp`
   - **Arguments tab → VM arguments:**
     ```
     --module-path "PATH_TO_FX" --add-modules javafx.controls,javafx.fxml
     ```
   - **Arguments tab → Working directory:** project root.
4. **Run**.

---

## 4. Option B — Run from the Command Line

From the project root:

### Compile
```bash
# macOS / Linux
mkdir -p out
find src -name "*.java" > sources.txt
javac --module-path "PATH_TO_FX" --add-modules javafx.controls,javafx.fxml \
      -d out @sources.txt
```

```powershell
# Windows PowerShell
New-Item -ItemType Directory -Force out | Out-Null
Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName } | Out-File sources.txt -Encoding ASCII
javac --module-path "PATH_TO_FX" --add-modules javafx.controls,javafx.fxml `
      -d out "@sources.txt"
```

### Run
```bash
# macOS / Linux
java --module-path "PATH_TO_FX" --add-modules javafx.controls,javafx.fxml \
     -cp out game.gui.GameApp
```

```powershell
# Windows
java --module-path "PATH_TO_FX" --add-modules javafx.controls,javafx.fxml `
     -cp out game.gui.GameApp
```

> 💡 You can also run the prebuilt classes that ship in `bin/` instead of recompiling, by replacing `-cp out` with `-cp bin`.

---

## 5. Running the Tests

The repo includes JUnit test classes under `src/game/tests/`:
- `Milestone1PublicTests.java`
- `Milestone2PublicTests.java`

To run them you need **JUnit 4** + **Hamcrest** on the classpath.

### From IntelliJ / Eclipse
Right-click the test file → **Run as JUnit Test**. The IDE will offer to add JUnit to the classpath if it isn't already there.

### From the command line
```bash
java --module-path "PATH_TO_FX" --add-modules javafx.controls,javafx.fxml \
     -cp out:junit-4.13.2.jar:hamcrest-core-1.3.jar \
     org.junit.runner.JUnitCore game.tests.Milestone1PublicTests
```
(Use `;` instead of `:` on Windows.)

---

## 6. Common Issues

| Symptom | Fix |
|---------|-----|
| `Error: JavaFX runtime components are missing` | You forgot `--module-path` / `--add-modules`. Re-check VM options. |
| `java.io.FileNotFoundException: titans.csv` | The working directory isn't the project root. Set it explicitly in the run config. |
| `UnsupportedClassVersionError` | The compiled `.class` files target a newer JDK than the one you're running. Recompile, or upgrade your `java` runtime. |
| Blank window / no UI | Make sure you're on Java 11+ with a matching JavaFX SDK version (don't mix Java 8 with FX 17). |

---

## 7. Quick Reference

| What | Value |
|------|-------|
| Main class | `game.gui.GameApp` |
| Working dir | project root (folder containing `titans.csv`) |
| Required JavaFX modules | `javafx.controls`, `javafx.fxml` |
| Window size | 1200 × 750 (non-resizable) |
