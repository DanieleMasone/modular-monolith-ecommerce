(function () {
  const storageKey = "portfolio-dashboard-theme";
  const toggle = document.querySelector("[data-theme-toggle]");

  function storeTheme(theme) {
    try {
      localStorage.setItem(storageKey, theme);
    } catch {
      // The selected theme still applies for the current page load.
    }
  }

  function applyTheme(theme, persist) {
    document.documentElement.dataset.theme = theme;
    if (persist) {
      storeTheme(theme);
    }

    if (toggle) {
      const nextTheme = theme === "dark" ? "light" : "dark";
      toggle.textContent = `${nextTheme[0].toUpperCase()}${nextTheme.slice(1)} mode`;
      toggle.setAttribute("aria-label", `Switch to ${nextTheme} mode`);
      toggle.setAttribute("aria-pressed", String(theme === "light"));
    }

    window.dispatchEvent(new CustomEvent("site-theme-change", { detail: { theme } }));
  }

  applyTheme(document.documentElement.dataset.theme || "dark", false);

  toggle?.addEventListener("click", () => {
    const currentTheme = document.documentElement.dataset.theme || "dark";
    applyTheme(currentTheme === "dark" ? "light" : "dark", true);
  });
})();
