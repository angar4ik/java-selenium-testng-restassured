# Java Selenium TestNG REST Assured Boilerplate

A boilerplate project for UI and API test automation using **Selenium WebDriver**, **TestNG**, **REST Assured**, and **ExtentReports**.

## Tech Stack

| Technology | Purpose |
|---|---|
| **Java 21** | Language |
| **TestNG** | Test runner & orchestration |
| **Selenium WebDriver** | Browser UI automation |
| **WebDriverManager** | Automatic browser driver management |
| **REST Assured** | API testing |
| **AssertJ** | Fluent assertions |
| **Jackson** | JSON / POJO mapping |
| **Lombok** | POJO boilerplate reduction |
| **ExtentReports** | Rich HTML test reports |
| **SLF4J** | Logging |
| **Maven** | Build & dependency management |

## Project Structure

```
├── .github/workflows/
│   ├── tests.yml                  # CI workflow — GH-hosted runner (manual dispatch)
│   └── tests-hetzner-runner.yml   # CI workflow — self-hosted Hetzner runner (manual dispatch)
├── pom.xml                          # Maven configuration
├── testng.xml                       # TestNG suite definition
├── src/
│   ├── main/java/com/example/
│   │   ├── api/base/                # API test base classes
│   │   ├── api/pojos/               # API request/response POJOs
│   │   ├── api/tests/               # API test source (stubs)
│   │   ├── config/                  # Configuration helpers
│   │   └── ui/                      # UI test source (stubs)
│   └── test/java/com/example/
│       ├── api/
│       │   ├── base/                # API test setup (RequestSpecification, etc.)
│       │   ├── pojos/               # API POJOs (User, Todo, etc.)
│       │   └── tests/               # API tests (ReqRes, JSONPlaceholder)
│       ├── config/
│       │   ├── EnvLoader.java       # .env file loader
│       │   └── TestConfig.java      # Centralised config
│       ├── report/
│       │   ├── ExtentReportManager.java
│       │   ├── ExtentTestListener.java  # TestNG listener → ExtentReports
│       │   └── DriverManager.java       # WebDriver lifecycle
│       └── ui/
│           ├── base/                # Base UI test class
│           ├── pages/               # Page Object Model (LoginPage, CheckboxesPage)
│           └── tests/               # UI tests (LoginTests, FormAndElementsTests)
```

## Prerequisites

- **Java 21** (JDK)
- **Maven** 3.9+
- A modern browser (Chrome, Firefox, etc.)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/angar4ik/java-selenium-testng-restassured.git
cd java-selenium-testng-restassured
```

### 2. Environment variables

Copy the example env file and fill in your API key:

```bash
cp .env.example .env
```

The `.env` file is ignored by Git. The `REQRES_API_KEY` secret is required for certain API tests.

### 3. Run all tests

```bash
mvn test
```

### 4. Run a specific TestNG suite

```bash
mvn test -DsuiteXmlFile=testng.xml
```

### CI / CD (GitHub Actions)

Two workflows are available, both triggered **manually** via `workflow_dispatch` in the GitHub UI:

#### Run Tests (GH Runner)

Runs tests on a **GitHub-hosted** Ubuntu runner.

1. Go to the **Actions** tab
2. Select **Run Tests (GH Runner)**
3. Optionally choose a custom suite XML file
4. Click **Run workflow**

#### Run Tests on Hetzner (Self-Hosted Runner)

Provisiones a **Hetzner Cloud** server on-the-fly, installs a self-hosted GitHub Actions runner, executes the tests remotely, and tears everything down automatically.

1. Go to the **Actions** tab
2. Select **Run Tests on Hetzner (Self-Hosted Runner)**
3. Choose a TestNG suite, server type (default `cx23`), and datacenter location (default `nbg1`)
4. Click **Run workflow**

**What happens under the hood:**

| Stage | Description |
|---|---|
| **Provision** | Generates a temporary SSH key, uploads it to Hetzner, creates a cloud server, waits for it to boot. |
| **Setup** | Installs Chrome, Xvfb (virtual display), Maven, and other browser dependencies via cloud-init + SSH. |
| **Runner** | Downloads and registers an **ephemeral** self-hosted GitHub Actions runner on the Hetzner server. |
| **Test** | The `test` job runs on the self-hosted runner — checks out the repo, sets up JDK 21, restores Maven cache, and executes `mvn test` with the chosen suite. Xvfb provides a virtual display for headless UI tests. |
| **Cleanup** | After the tests finish (success or failure), the Hetzner server and temporary SSH key are deleted so nothing is left behind. |

**Required secrets** (configured in the `test` environment):

| Secret | Purpose |
|---|---|
| `HCLOUD_TOKEN` | Hetzner Cloud API token (server create/delete) |
| `GH_PAT` | GitHub Personal Access Token with `repo` scope (runner registration) |
| `REQRES_API_KEY` | API key for ReqRes tests |

After execution, **ExtentReports** HTML reports are uploaded as a build artifact.

## Reporting

[ExtentReports](https://www.extentreports.com/) generates a rich HTML report with:

- Test pass / fail / skip summary
- Step-by-step logs with screenshots (UI tests)
- Timeline and dashboard views

Reports are available at `test-output/extent-reports/` after a local run, or as a downloadable artifact in GitHub Actions.

## License

[MIT](LICENSE.md)
