# Getting Started

This is how to get a new person set up to write code for the team. When you're done you'll be able to:

1. Clone, pull, and push code with git and GitHub
2. Build the code in Android Studio and load it onto a robot

It works for Apple silicon Macs and Windows. When the steps are different I'll say Mac or Windows. Plan on
about an hour, and most of that is waiting on downloads.

Before you start, get the login and password for our shared GitHub account (EB-Programmer) from a mentor.
You'll need it in Part 1. Don't post it anywhere public.

If you're at school, use a mobile hotspot from your phone instead of the school Wi-Fi. The school network
rarely lets you download big things and sometimes blocks github.com completely. That goes for git (clone, pull,
push) and for downloads (Git, Android Studio, the Android SDK, Gradle sync). Android Studio and the first sync
are several GB, so doing those at home is even better.

---

## Part 1: Git and GitHub

We all share one GitHub account, EB-Programmer. Each person still makes their own SSH key (a file on your
computer that proves to GitHub who you are) and adds it to that account.

If you're on Windows and would rather click buttons than type commands, you can use GitHub Desktop and skip to
section 1.8. It doesn't need SSH keys.

### 1.1 Install Git

Mac: open Terminal and run `git --version`. If Git isn't installed, macOS will offer to install the command
line tools. Say yes and wait for it to finish.

Windows: install Git for Windows from <https://git-scm.com/download/win>. The default options are fine. It comes
with Git Bash, which is a terminal that works like the Mac one. Use Git Bash for every command in this guide.

### 1.2 Tell git who you are

Since everyone uses the same account, every commit shows up as EB-Programmer, so set it that way:

```bash
git config --global user.name "EB-Programmer"
git config --global user.email "eb_programmer@yahoo.com"
```

### 1.3 Make your SSH key

```bash
ssh-keygen -t ed25519 -C "your-name-and-computer" -f ~/.ssh/id_ed25519_eb
```

Hit Enter at the passphrase prompt to skip it, or set one if you want. This makes two files in `~/.ssh/`.
`id_ed25519_eb` is private, so never share it or upload it. `id_ed25519_eb.pub` is the public one.

Print the public key and copy the whole line (it starts with `ssh-ed25519`):

```bash
cat ~/.ssh/id_ed25519_eb.pub
```

### 1.4 Add the key to the GitHub account

1. Go to <https://github.com> and sign in as EB-Programmer. GitHub will ask for a 2FA code, which gets texted
   to Coach Robert, so you'll need to ask him for it.
2. Click the profile picture in the top right, then Settings, then SSH and GPG keys, then New SSH key.
3. For the title, put your name and computer, like `Sam - Windows laptop`, so we can tell the keys apart.
4. Paste the key you copied and click Add SSH key.

### 1.5 Tell SSH to use that key for our repo

If you ever use a personal GitHub account on the same computer, SSH needs to know which key goes with which
account. Our repo address uses a made-up host name, `eb.github.com`, and this config entry is what connects it
to the right key.

Open (or create) `~/.ssh/config` and add this:

```
# EB-Programmer GitHub
Host eb.github.com
    HostName github.com
    User git
    PreferredAuthentications publickey
    IdentityFile ~/.ssh/id_ed25519_eb
    IdentitiesOnly yes
```

On a Mac you can do this with `nano ~/.ssh/config` (save with Ctrl+O, Enter, then Ctrl+X). On Windows, in Git
Bash, use `notepad ~/.ssh/config`. If Notepad tacks `.txt` onto the filename, rename the file so it has no
extension.

Then test it:

```bash
ssh -T git@eb.github.com
```

The first time it'll ask about a fingerprint, so type `yes`. You should get back something like
`Hi EB-Programmer! You've successfully authenticated, but GitHub does not provide shell access.`

### 1.6 Clone the repo

Pick a folder for your code. Try to avoid spaces in the path. Then:

```bash
mkdir -p ~/Development && cd ~/Development
git clone git@eb.github.com:EB-Programmer/FtcRobotControllerDecode.git
```

The repo name might change from season to season, so if the clone fails, check the name on GitHub. Keep
`eb.github.com` as the host in the address, don't switch it to `github.com`.

### 1.7 Pulling, committing, and pushing

```bash
git pull                       # get everyone else's changes. Do this before you start working
git status                     # see what you changed
git add -A                     # stage your changes
git commit -m "Add shooter power tuning to teleop"
git push                       # send your commits to GitHub
```

We work straight on `main`. No branches, no pull requests, just pull, commit, and push.

Pull before you start and again before you push. If two people changed the same file, git might ask you to sort
out a merge conflict, and the first time that happens, grab a teammate. Commit small and often, and don't push
code that doesn't build.

### 1.8 Windows option: GitHub Desktop

If you use this you can skip 1.1 through 1.7. You don't need Git Bash, an SSH key, or the config entry.

1. Download and install GitHub Desktop from <https://desktop.github.com/>.
2. Open it and choose Sign in to GitHub.com. Your browser opens, so sign in as EB-Programmer. Coach Robert gets
   the 2FA text. Click Authorize and go back to GitHub Desktop.
3. When it asks for your name and email, use EB-Programmer and eb_programmer@yahoo.com.
4. Go to File, then Clone repository, then URL. Enter `https://github.com/EB-Programmer/FtcRobotControllerDecode`,
   pick a folder (for example `C:\Users\<you>\Development`), and click Clone.
5. To use it day to day: Fetch origin / Pull origin at the top gets everyone's changes (do it before you start).
   Your changed files show up on the left, so type a short summary in the box at the bottom left and click
   Commit to main. Then click Push origin at the top.

---

## Part 2: Android Studio

Android Studio is what we use to edit the code, build the robot app, and load it onto the Control Hub.

I'd recommend watching [Coach Pratt's setup video](https://www.youtube.com/watch?v=_ZIYtNadJBo) once. It's what
this part is based on. If anything here doesn't match the video, trust the video and fix this doc. FIRST also has
a text version
[here](https://ftc-docs.firstinspires.org/programming_resources/android_studio_java/Android-Studio-Tutorial.html),
but the video points out that parts of it are out of date.

### 2.1 Download and install

Download it from <https://developer.android.com/studio>. Get the latest version. The FTC docs talk about
Ladybug (2024.2) as the minimum, but newer is fine, and being a few versions behind doesn't matter either.

On a Mac, make sure you pick the Apple chip version, not Intel. Open the `.dmg` and drag Android Studio into
Applications. On Windows, run the installer and take the defaults.

Launch it. If it asks about sending usage data to Google, either answer is fine. In the setup wizard choose
Standard. It will download the Android SDK, which is several GB.

One thing to watch out for: never upgrade Gradle. If Android Studio offers to upgrade the Gradle version or the
Android Gradle Plugin for the project, say no (Not now, or Don't remind me). If it gets upgraded by accident, the
JDK setting resets and you'll have to set it back to 17 (see 2.3).

### 2.2 Open the project

1. Go to File, then Open.
2. Pick the folder you cloned (`FtcRobotControllerDecode`). It should be the one that has `build.gradle` and
   `TeamCode` in it, not a folder inside it.
3. If it asks, choose Trust Project.
4. Wait for the Gradle sync to finish (there's a progress bar at the bottom). The first one can take 5 to 15
   minutes.

If the first sync fails with a Java or JDK error, that's expected. Do 2.3, then sync again. If it complains about
a missing SDK or component, click the link in the error and let it install.

### 2.3 Project settings (JDK 17)

The FTC SDK only works with JDK 17. If you use a newer Java, the project won't build.

1. Download JDK 17 from Oracle's Java archive:
   <https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html>. On an Apple silicon Mac get
   the macOS Arm 64 DMG Installer. On Windows get the x64 Installer.
2. Run the installer and take the defaults. It may ask for your computer password.
3. In Android Studio, open Settings (Mac: Android Studio menu, then Settings. Windows: File, then Settings) and
   go to Build, Execution, Deployment, then Build Tools, then Gradle. Under Gradle JDK, pick the 17 entry from
   the drop-down. If it isn't listed, click the folder icon and browse to the JDK 17 folder. Click OK.
4. Do File, then Sync Project with Gradle Files, and make sure it works.

The project also builds against Android SDK 36. Android Studio usually offers to download it during the sync. If
not, check Tools, then SDK Manager, then SDK Platforms.

### 2.4 Build the code

Select the TeamCode module and do Build, then Make Project, or just try to run it (next section). A good build
ends with `BUILD SUCCESSFUL`. Errors show up in the Build panel at the bottom.

Write your code in the `TeamCode` folder, not in `FtcRobotController`. All of ours is in
`TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`. `FtcRobotController` is the official FTC SDK. It does
have a bunch of well commented example opmodes in
`FtcRobotController/src/main/java/org/firstinspires/ftc/robotcontroller/external/samples`, and those are worth
reading when you need to figure out how to use a sensor or motor.

### 2.5 Load code onto the robot

We just use a USB-C cable.

1. Plug the Control Hub into your computer with the USB-C cable and make sure the Control Hub is on.
2. In the device drop-down at the top of Android Studio, check that the Control Hub shows up. It looks
   something like "Control Hub v1.0".
3. In the run configuration drop-down next to it, choose TeamCode.
4. Click the green play button. It builds and installs, which takes about a minute.
5. On the Driver Station, your opmodes will be in the Autonomous and TeleOp lists. An opmode only shows up if
   it isn't marked `@Disabled`.

---

## Part 3: Other stuff worth knowing

- A class with `@Disabled` above it is hidden from the Driver Station. Take it off to make the opmode show up.
  Lots of our older opmodes are disabled on purpose.
- We draw autonomous paths at <https://visualizer.pedropathing.com/>, click the `</>` button to export the
  code, and paste it into an auton opmode. The saved `.pp` path files are in the repo root.
- The code finds motors and servos by the names in the robot's hardware configuration (like `leftFrontDrive` or
  `shooter`). If a name in the code doesn't match the config on the Control Hub, the opmode crashes when you
  press INIT.
- The top of each teleop file has a comment listing the gamepad controls.
- If you get stuck, ask in the team chat. The [FTC docs](https://ftc-docs.firstinspires.org/) are also good.

---

## Troubleshooting

| Problem | Try |
|---|---|
| Clone, pull, or push hangs or fails at school, or a download won't start | The school Wi-Fi is probably blocking it. Switch to a phone's mobile hotspot. |
| `Permission denied (publickey)` when cloning or pushing | The key isn't added to GitHub (1.4) or `~/.ssh/config` is wrong (1.5). Run `ssh -T git@eb.github.com` again. |
| `Could not resolve hostname eb.github.com` | The `Host eb.github.com` entry in `~/.ssh/config` is missing or misspelled. |
| Gradle sync fails with a Java or JDK error | Make sure Gradle JDK is set to 17 (2.3). |
| Android Studio wants to upgrade Gradle | Say no. If it already happened, set Gradle JDK back to 17 (2.3). |
| Control Hub isn't in the device list | Check the USB-C cable at both ends and that the Control Hub is on. Try a different cable or port. |
| An opmode isn't on the Driver Station | It's probably marked `@Disabled`. |

---

## Quick workflow: new code onto the robot

Once you're set up, this is all you need:

1. Run `git pull` in the repo folder (on a hotspot if you're at school). In GitHub Desktop, click Fetch origin,
   then Pull origin.
2. Open the project in Android Studio and let Gradle finish syncing if it starts.
3. Plug the Control Hub into your computer with the USB-C cable.
4. Click the green play button with TeamCode selected, and wait for it to install.
5. Pick your opmode on the Driver Station.

If it doesn't work, see 2.5 or the troubleshooting table above.
