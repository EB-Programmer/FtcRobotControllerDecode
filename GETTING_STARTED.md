# Getting Started (Electric Bacon FTC Programming)

This guide gets a new team member from a blank laptop to:

1. **Git + GitHub**: able to clone, pull, and push code
2. **Android Studio**: able to build the code and load it onto a robot

It works on **Apple silicon Macs** and **Windows**. Steps that differ are marked 🍎 Mac or 🪟 Windows.
Plan on about an hour, most of it downloads.

> **Before you start:** ask a mentor or the team programming lead for the **login and password for the shared
> GitHub account (`EB-Programmer`)**. You'll need it in Part 1. Don't post it anywhere public.

> **At school? Use a mobile hotspot.** The school Wi-Fi often blocks large downloads and sometimes
> `github.com` itself. When you're at school and need to use git (clone, pull, push) or download anything
> (Git, Android Studio, the Android SDK, Gradle sync), turn on your phone's **mobile hotspot** and connect your
> computer to it instead. The Android Studio download and first project sync are several GB, so a hotspot is best
> for those, or do them at home on your own Wi-Fi.

---

## Part 1: Git and GitHub

Our team uses **one shared GitHub account, `EB-Programmer`**. Each person still gets their own SSH key
(a file on your computer that proves to GitHub that it's you), and you add that key to the shared account.

### 1.1 Install Git

- 🍎 **Mac:** open the **Terminal** app and run `git --version`. If Git isn't installed, macOS offers to install
  the Command Line Tools. Click **Install** and wait for it to finish.
- 🪟 **Windows:** install **Git for Windows** from <https://git-scm.com/download/win>. The default options are
  fine. This also installs **Git Bash**, a terminal that works like the Mac one.
  **Use Git Bash for every command in this guide.**

### 1.2 Tell Git who you are

The shared account means every commit would look like it came from `EB-Programmer`. Set your commit name to
`EB-Programmer` too, for consistency.

```bash
git config --global user.name "EB-Programmer"
git config --global user.email "eb_programmer@yahoo.com"
```

### 1.3 Create your SSH key

```bash
ssh-keygen -t ed25519 -C "your-name-and-computer" -f ~/.ssh/id_ed25519_eb
```

- Press **Enter** at the passphrase prompt to skip it, or set one if you like.
- This creates two files in `~/.ssh/`: `id_ed25519_eb` (**private. Never share or upload it**) and
  `id_ed25519_eb.pub` (public, safe to share).

Show the public key and copy the whole line, starting with `ssh-ed25519`:

```bash
cat ~/.ssh/id_ed25519_eb.pub
```

### 1.4 Add your key to the shared GitHub account

1. Go to <https://github.com> and sign in as **EB-Programmer** (login and password from your mentor).
   GitHub will ask for a verification code (2FA). The code is texted to **Coach Robert**, so ask him for it
   when you sign in.
2. Click the profile picture (top right), then **Settings**, then **SSH and GPG keys**, then **New SSH key**.
3. **Title:** your name and computer, e.g. `Sam - Windows laptop`, so we can tell the keys apart.
4. **Key:** paste the line you copied. Click **Add SSH key**.

### 1.5 Tell SSH to use that key for our repo

You may someday use a personal GitHub account on the same computer. A small config entry makes sure the right key
is used for the team repo. Our repo's address uses the host name `eb.github.com` for this reason.

Open (or create) the file `~/.ssh/config` and add:

```
# EB-Programmer GitHub
Host eb.github.com
    HostName github.com
    User git
    PreferredAuthentications publickey
    IdentityFile ~/.ssh/id_ed25519_eb
    IdentitiesOnly yes
```

- 🍎 Mac: `nano ~/.ssh/config` (save with Ctrl+O, Enter, Ctrl+X).
- 🪟 Windows: in Git Bash, `notepad ~/.ssh/config`. If Notepad adds `.txt` to the name, rename the file so it has
  no extension.

Test it:

```bash
ssh -T git@eb.github.com
```

The first time, type `yes` when it asks about the host's fingerprint. Success looks like
`Hi EB-Programmer! You've successfully authenticated, but GitHub does not provide shell access.`

### 1.6 Clone the repo

Choose a folder for your code (a path **without spaces** is safest) and clone:

```bash
mkdir -p ~/Development && cd ~/Development
git clone git@eb.github.com:EB-Programmer/FtcRobotControllerDecode.git
```

> The repository name may change for new seasons. Check the repo page on GitHub if the clone fails.
> Always keep `eb.github.com` as the host part of the address, not `github.com`.

### 1.7 Everyday git: pull, commit, push

```bash
git pull                       # get everyone else's latest changes. Do this BEFORE you start working
git status                     # see what you changed
git add -A                     # stage your changes
git commit -m "Add shooter power tuning to teleop"
git push                       # send your commits to GitHub
```

Tips:

- **We work directly on `main`.** There are no branches or pull requests. Just pull, commit and push.
- **Pull before you start, and again before you push.** If two people edit the same file, Git may ask you to
  resolve a *merge conflict*. Ask a teammate for help the first time.
- Commit small and often. Never commit code that doesn't build.

---

## Part 2: Android Studio

Android Studio is the program we use to edit the code, build the robot app, and load it onto the Control Hub (or
Robot Controller phone).

### 2.1 Download and install

1. Download from <https://developer.android.com/studio>.
   - 🍎 Choose **Mac with Apple chip** (not Intel).
   - 🪟 Choose the Windows `.exe`.
2. Install it.
   - 🍎 Open the `.dmg` and drag **Android Studio** into **Applications**.
   - 🪟 Run the installer with the default options.
3. Launch it. In the setup wizard choose **Standard** installation. It downloads the Android SDK (several GB,
   so use good Wi-Fi).

**Version:** this project needs **Android Studio Ladybug (2024.2) or newer**. It uses Android Gradle Plugin 8.7.0 and
Gradle 8.9. If Android Studio ever offers to **upgrade the Android Gradle Plugin or Gradle ("AGP Upgrade
Assistant")**, click **Don't remind me / Not now**. Upgrading can break the FTC SDK.

### 2.2 Open the project

1. **File → Open** (or **Open** on the welcome screen).
2. Select the **folder you cloned** (`FtcRobotControllerDecode`). The folder that contains `build.gradle` and
   `TeamCode`, not a subfolder.
3. If asked, choose **Trust Project**.
4. Wait for the **Gradle sync** to finish (progress bar at the bottom). The first sync downloads a lot and can
   take 5 to 15 minutes.

If the first sync fails with a Java or JDK error, that's expected. Set up **JDK 17** as described in 2.3, then sync again.

If the sync complains about a missing SDK or component, click the link in the error message to install it.

### 2.3 Unique settings for this project

> The team's setup guide video is [Brogan's Android Studio setup video](https://www.youtube.com/watch?v=_ZIYtNadJBo).
> This section was written from the project's build files, so **follow the video if anything below disagrees
> with it**, and then fix this guide.

- **Gradle JDK must be version 17 (important):** the project only builds with **JDK 17**. Don't use the default
  or a newer version.
  1. **Install JDK 17.** The easiest way is inside Android Studio. Open **Settings** (🍎 *Android Studio → Settings*,
     🪟 *File → Settings*), go to **Build, Execution, Deployment → Build Tools → Gradle**, open the
     **Gradle JDK** drop-down and choose **Download JDK…**. Pick version **17** and click **Download**.
     (Alternatively, install JDK 17 yourself, for example Eclipse Temurin 17 from <https://adoptium.net/>.)
  2. **Select it.** In that same **Gradle JDK** drop-down, choose the **17** entry and click **OK**.
  3. Run **File → Sync Project with Gradle Files** and make sure it succeeds.
- **SDK platform:** the project compiles against **Android SDK 36**. Android Studio normally offers to download it
  during sync. You can also check **Tools → SDK Manager → SDK Platforms**.
- **Official FTC reference:** FIRST's own walkthrough is at
  <https://ftc-docs.firstinspires.org/programming_resources/android_studio_java/Android-Studio-Tutorial.html>.

### 2.4 Build the code

- Select the **TeamCode** module and run **Build → Make Project**, or just try to run it (next step).
- A successful build ends with `BUILD SUCCESSFUL`. Errors appear in the **Build** panel at the bottom.

All of our code is in
`TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`.

### 2.5 Load code onto the robot

You install the app on the **Control Hub** over a USB-C cable.

1. Connect your computer to the Control Hub with a USB-C cable, using the Control Hub's USB-C port, and
   make sure the Control Hub is powered on.
2. In the device drop-down at the top of Android Studio, make sure the Control Hub shows up
   (it appears as something like *Control Hub v1.0*).
3. In the run-configuration drop-down, choose **TeamCode**.
4. Click the green ▶ **Run** button. The app builds and installs, which takes about a minute.
5. On the Driver Station, your opmodes appear in the **Autonomous** and **TeleOp** lists. An opmode only shows up
   if it's **not** marked `@Disabled`.

---

## Part 3: Other things worth knowing

- **Opmodes and `@Disabled`:** a class with `@Disabled` above it is hidden from the Driver Station. Remove it to
  make an opmode appear. Many of our older opmodes are disabled on purpose.
- **Pedro Pathing autonomous paths:** we draw paths at <https://visualizer.pedropathing.com/>, export the code
  with the `</>` button, and paste it into an auton opmode. Saved `.pp` path files are kept in the repo root.
- **Hardware names:** the code looks up motors and servos by the name in the robot's *hardware configuration*
  (for example `leftFrontDrive`, `shooter`). If a name in the code doesn't match the config on the Control Hub,
  the opmode crashes when you press INIT.
- **Gamepad controls:** the top of each teleop file has a comment listing the controls.
- **Where to get help:** ask in the team chat, or check the
  [FTC docs](https://ftc-docs.firstinspires.org/).

---

## Quick troubleshooting

| Problem | Try |
|---|---|
| Clone, pull or push hangs or fails at school, or a download won't start | The school Wi-Fi is probably blocking it. Switch your computer to a phone's mobile hotspot and try again. |
| `Permission denied (publickey)` when cloning or pushing | The key isn't added to GitHub (1.4) or `~/.ssh/config` is wrong (1.5). Re-run `ssh -T git@eb.github.com`. |
| Clone says `Could not resolve hostname eb.github.com` | The `Host eb.github.com` entry in `~/.ssh/config` is missing or misspelled. |
| Gradle sync fails with a Java or JDK error | Make sure **Gradle JDK** is set to version **17** (2.3). |
| Android Studio wants to upgrade Gradle/AGP | Decline. See 2.1. |
| Control Hub doesn't appear in the device list | Check that the USB-C cable is plugged in at both ends and the Control Hub is powered on. Try a different cable or port. |
| An opmode isn't on the Driver Station | It's probably marked `@Disabled`. |

---

## Quick Workflow: Get the Latest Code onto the Robot

Once you're set up, this is all you need:

1. **Pull** (on a mobile hotspot if you're at school): in a terminal (Git Bash on Windows) inside the repo folder, run `git pull`.
2. **Open the project** in Android Studio and let Gradle finish syncing if it starts one.
3. **Connect to the robot:** plug the Control Hub into your computer with the USB-C cable.
4. **Press the green ▶ Run button** (top toolbar, with **TeamCode** selected). Wait for it to install.
5. Pick your opmode on the Driver Station.

If it doesn't work, see section 2.5 or the troubleshooting table above.
