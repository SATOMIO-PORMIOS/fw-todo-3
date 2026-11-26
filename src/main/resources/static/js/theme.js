(() => {
    const storageKey = "todoapp-theme";
    const savedTheme = localStorage.getItem(storageKey);
    document.body.dataset.theme = savedTheme === "light" ? "light" : "dark";

    const toggle = document.querySelector("[data-theme-toggle]");
    if (!toggle) return;

    const updateLabel = () => {
        const isLight = document.body.dataset.theme === "light";
        toggle.setAttribute("aria-label", isLight ? "暗い画面に切り替え" : "明るい画面に切り替え");
        toggle.setAttribute("aria-pressed", String(isLight));
        toggle.title = isLight ? "暗い画面に切り替え" : "明るい画面に切り替え";
    };

    updateLabel();
    toggle.addEventListener("click", () => {
        const nextTheme = document.body.dataset.theme === "light" ? "dark" : "light";
        document.body.dataset.theme = nextTheme;
        localStorage.setItem(storageKey, nextTheme);
        updateLabel();
    });
})();
