#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

PAGES_DIR="${PAGES_DIR:-pages}"
QUALITY_REPORTS_DIR="${QUALITY_REPORTS_DIR:-quality-reports}"

fail() {
  echo "Pages assembly failed: $*" >&2
  exit 1
}

require_file() {
  [[ -f "$1" ]] || fail "missing required file: $1"
}

require_dir() {
  [[ -d "$1" ]] || fail "missing required directory: $1"
}

case "$PAGES_DIR" in
  "" | "." | "/" | "\\" )
    fail "unsafe PAGES_DIR value: $PAGES_DIR"
    ;;
esac

write_openapi_ui() {
  cat > "$PAGES_DIR/openapi/index.html" <<'EOF'
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Modular Monolith E-commerce API</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://unpkg.com/swagger-ui-dist@5/swagger-ui.css">
</head>
<body>
  <div id="swagger-ui"></div>
  <script src="https://unpkg.com/swagger-ui-dist@5/swagger-ui-bundle.js"></script>
  <script>
    window.onload = () => {
      window.ui = SwaggerUIBundle({
        url: "./openapi.json",
        dom_id: "#swagger-ui",
        deepLinking: true,
        presets: [SwaggerUIBundle.presets.apis],
        layout: "BaseLayout"
      });
    };
  </script>
</body>
</html>
EOF
}

write_test_report_index() {
  cat > "$PAGES_DIR/test-report/index.html" <<'EOF'
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Test Reports</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="../assets/site.css">
</head>
<body>
  <main class="page-shell">
    <h1>HTML Test Reports</h1>
    <p>Generated from Maven Surefire and Failsafe XML outputs during CI.</p>
    <ul>
      <li><a href="./shared-kernel/surefire.html">shared-kernel Surefire report</a></li>
      <li><a href="./catalog/surefire.html">catalog Surefire report</a></li>
      <li><a href="./orders/surefire.html">orders Surefire report</a></li>
      <li><a href="./payment/surefire.html">payment Surefire report</a></li>
      <li><a href="./ecommerce-app/surefire.html">ecommerce-app Surefire report</a></li>
      <li><a href="./ecommerce-app/failsafe.html">ecommerce-app Failsafe report</a></li>
    </ul>
  </main>
</body>
</html>
EOF
}

write_site_css() {
  cat > "$PAGES_DIR/assets/site.css" <<'EOF'
:root {
  color-scheme: light;
  --bg: #f7f8fa;
  --text: #17202a;
  --muted: #5f6b7a;
  --line: #d8dee6;
  --panel: #ffffff;
  --accent: #146c63;
}

body {
  margin: 0;
  background: var(--bg);
  color: var(--text);
  font-family: ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
  line-height: 1.6;
}

.page-shell {
  width: min(1040px, calc(100% - 32px));
  margin: 0 auto;
  padding: 32px 0 56px;
}

.site-nav {
  border-bottom: 1px solid var(--line);
  background: var(--panel);
}

.site-nav .page-shell {
  padding: 14px 0;
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
}

a {
  color: #2d5f9a;
}

h1,
h2,
h3 {
  line-height: 1.2;
}

table {
  border-collapse: collapse;
  width: 100%;
  background: var(--panel);
}

th,
td {
  border: 1px solid var(--line);
  padding: 8px 10px;
  text-align: left;
}

pre {
  overflow-x: auto;
  padding: 14px;
  border-radius: 8px;
  background: #111827;
  color: #f9fafb;
}

code {
  border-radius: 4px;
}

.mermaid {
  background: var(--panel);
  border: 1px solid var(--line);
  border-radius: 8px;
  padding: 16px;
  overflow-x: auto;
}
EOF
}

write_jekyll_layout() {
  cat > "$PAGES_DIR/_layouts/default.html" <<'EOF'
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>{{ page.title | default: site.title }}</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="{{ '/assets/site.css' | relative_url }}">
</head>
<body>
  <nav class="site-nav">
    <div class="page-shell">
      <a href="{{ '/' | relative_url }}">Home</a>
      <a href="{{ '/docs/' | relative_url }}">Docs</a>
      <a href="{{ '/openapi/' | relative_url }}">OpenAPI</a>
      <a href="{{ '/javadoc/' | relative_url }}">JavaDoc</a>
      <a href="{{ '/coverage/' | relative_url }}">Coverage</a>
      <a href="{{ '/test-report/' | relative_url }}">Tests</a>
    </div>
  </nav>
  <main class="page-shell">
    {{ content }}
  </main>
  <script type="module">
    import mermaid from "https://cdn.jsdelivr.net/npm/mermaid@11/dist/mermaid.esm.min.mjs";
    document.querySelectorAll("pre").forEach((pre) => {
      const code = pre.querySelector("code");
      const classes = [
        pre.getAttribute("class") || "",
        code?.getAttribute("class") || "",
        pre.parentElement?.getAttribute("class") || "",
        pre.parentElement?.parentElement?.getAttribute("class") || ""
      ].join(" ");
      if (!classes.includes("language-mermaid")) {
        return;
      }
      const diagram = document.createElement("div");
      diagram.className = "mermaid";
      diagram.textContent = code ? code.textContent : pre.textContent;
      const wrapper = pre.closest(".language-mermaid") || pre;
      wrapper.replaceWith(diagram);
    });
    mermaid.initialize({ startOnLoad: true, securityLevel: "strict" });
  </script>
</body>
</html>
EOF

  cat > "$PAGES_DIR/_config.yml" <<'EOF'
title: Modular Monolith E-commerce
markdown: kramdown
plugins:
  - jekyll-relative-links
relative_links:
  enabled: true
  collections: true
EOF
}

copy_test_reports() {
  local -a reports=()

  if [[ -d "$QUALITY_REPORTS_DIR" ]]; then
    while IFS= read -r -d '' report; do
      reports+=("$report")
    done < <(find "$QUALITY_REPORTS_DIR" \( -name "surefire.html" -o -name "failsafe.html" \) -print0)
  fi

  if [[ ${#reports[@]} -eq 0 ]]; then
    while IFS= read -r -d '' report; do
      reports+=("$report")
    done < <(find . \
      \( -path "./$PAGES_DIR" -o -path "./_site" \) -prune \
      -o -path "*/target/reports/*" \( -name "surefire.html" -o -name "failsafe.html" \) -print0)
  fi

  [[ ${#reports[@]} -gt 0 ]] || fail "no Surefire or Failsafe HTML reports found"

  local report normalized module file
  for report in "${reports[@]}"; do
    normalized="${report#./}"
    if [[ "$normalized" == "$QUALITY_REPORTS_DIR/"* ]]; then
      normalized="${normalized#${QUALITY_REPORTS_DIR}/}"
    fi
    module="${normalized%%/target/reports/*}"
    file="$(basename "$report")"
    mkdir -p "$PAGES_DIR/test-report/$module"
    cp "$report" "$PAGES_DIR/test-report/$module/$file"
  done
}

add_jekyll_front_matter() {
  local file tmp
  while IFS= read -r -d '' file; do
    tmp="$(mktemp)"
    {
      printf -- "---\nlayout: default\n---\n\n"
      cat "$file"
    } > "$tmp"
    mv "$tmp" "$file"
  done < <(find "$PAGES_DIR/docs" -name "*.md" -print0)
}

validate_pages_source() {
  require_file "$PAGES_DIR/index.html"
  require_file "$PAGES_DIR/openapi/index.html"
  require_file "$PAGES_DIR/openapi/openapi.json"
  require_file "$PAGES_DIR/docs/index.md"
  require_file "$PAGES_DIR/docs/architecture.md"
  require_file "$PAGES_DIR/docs/business-flow.md"
  require_file "$PAGES_DIR/docs/trade-offs.md"
  require_file "$PAGES_DIR/docs/adr/index.md"
  require_file "$PAGES_DIR/javadoc/index.html"
  require_file "$PAGES_DIR/coverage/index.html"
  require_file "$PAGES_DIR/test-report/index.html"
  require_file "$PAGES_DIR/test-report/shared-kernel/surefire.html"
  require_file "$PAGES_DIR/test-report/catalog/surefire.html"
  require_file "$PAGES_DIR/test-report/orders/surefire.html"
  require_file "$PAGES_DIR/test-report/payment/surefire.html"
  require_file "$PAGES_DIR/test-report/ecommerce-app/surefire.html"
  require_file "$PAGES_DIR/test-report/ecommerce-app/failsafe.html"

  if [[ -e "$PAGES_DIR/dashboard" ]]; then
    fail "dashboard alias should not be published"
  fi
}

require_file "dashboard/index.html"
require_dir "docs"
require_file "ecommerce-app/target/generated-docs/openapi.json"
require_dir "target/reports/apidocs"

COVERAGE_SOURCE="$QUALITY_REPORTS_DIR/coverage-report/target/site/jacoco-aggregate"
if [[ ! -d "$COVERAGE_SOURCE" ]]; then
  COVERAGE_SOURCE="coverage-report/target/site/jacoco-aggregate"
fi
require_dir "$COVERAGE_SOURCE"

rm -rf "$PAGES_DIR" _site
mkdir -p "$PAGES_DIR"/{_layouts,assets,docs,openapi,javadoc,coverage,test-report}

cp dashboard/index.html "$PAGES_DIR/index.html"
cp -R docs/. "$PAGES_DIR/docs/"
add_jekyll_front_matter

cp ecommerce-app/target/generated-docs/openapi.json "$PAGES_DIR/openapi/openapi.json"
write_openapi_ui

cp -R target/reports/apidocs/. "$PAGES_DIR/javadoc/"
cp -R "$COVERAGE_SOURCE"/. "$PAGES_DIR/coverage/"

write_test_report_index
copy_test_reports
write_site_css
write_jekyll_layout
validate_pages_source

echo "Pages source assembled in $PAGES_DIR"
