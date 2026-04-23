# Vue & Tailwind 卡片组件规范

【技术栈要求】 
请使用 Vue 和 Tailwind CSS 编写组件。

【动画与微交互要求】
1. 动效基础：必须包含针对性的过渡属性（如 transition-transform transition-shadow）与适中时长（如 duration-300），使用弹性曲线（假设已配置 ease-spring）。为保证移动端丝滑，添加 will-change-transform；同时出于无障碍考虑，请添加 motion-reduce:transition-none。
2. 悬浮与按压反馈：鼠标悬浮时，卡片整体明显上浮（-translate-y-2）并加深阴影。鼠标按压时，有真实的微小下陷反馈（active:scale-[0.98]）。需包含键盘焦点状态（focus-visible:ring）和 cursor-pointer。
3. 嵌套联动：必须使用 Tailwind 的 group 机制实现内外联动。卡片悬浮时，内部图标/装饰放大或位移（如 group-hover:scale-110 group-hover:translate-x-1），点击时回缩（group-active:scale-95），并配合色彩平滑过渡。