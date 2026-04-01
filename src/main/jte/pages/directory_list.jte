@param String title
@param String metaDescription = null
@param String canonicalUrl = null
@param java.util.List<String> breadcrumbs
@param java.util.List<java.util.Map.Entry<String, String>> featuredItems = java.util.List.of()
@param java.util.List<java.util.Map.Entry<String, String>> items

@template.layout(
    title = title,
    description = metaDescription,
    canonical = canonicalUrl,
    content = @`
    <div class="static-page-container" style="max-width: 800px; margin: 0 auto; padding: 4rem 1rem;">
        
        <!-- Breadcrumbs -->
        <div style="margin-bottom: 2rem; color: #666; font-size: 0.9rem;">
            <a href="/" style="color: #666; text-decoration: none;">Home</a>
            @for(String crumb : breadcrumbs)
                <span> / </span> <span>${crumb}</span>
            @endfor
        </div>

        <h1 style="font-size: 2.5rem; font-weight: 800; margin-bottom: 1rem;">${title}</h1>

        @if(metaDescription != null)
            <p style="font-size: 1.05rem; color: #64748b; line-height: 1.7; margin-bottom: 2rem; max-width: 720px;">
                ${metaDescription}
            </p>
        @endif

        @if(featuredItems != null && !featuredItems.isEmpty())
            <div style="margin-bottom: 2rem; padding: 1.25rem; border: 1px solid #dbeafe; border-radius: 16px; background: #f8fbff;">
                <div style="font-size: 0.78rem; font-weight: 800; letter-spacing: 0.08em; text-transform: uppercase; color: #1d4ed8; margin-bottom: 0.75rem;">
                    Priority Fix-or-Sell Pages
                </div>
                <div style="display: grid; gap: 0.75rem;">
                    @for(java.util.Map.Entry<String, String> item : featuredItems)
                        <a href="${item.getValue()}" style="display: block; text-decoration: none; color: #0f172a; font-weight: 800; padding: 0.9rem 1rem; border-radius: 12px; border: 1px solid #bfdbfe; background: #fff;">
                            ${item.getKey()} <span style="float: right; color: #1d4ed8;">-></span>
                            <div style="font-size: 0.82rem; color: #64748b; font-weight: 600; margin-top: 0.35rem;">
                                Open a year-specific decision page before you drill into the long-tail problem pages.
                            </div>
                        </a>
                    @endfor
                </div>
            </div>
        @endif

        <ul style="list-style: none; padding: 0;">
            @for(java.util.Map.Entry<String, String> item : items)
                <li style="margin-bottom: 1rem; padding: 1rem 1.1rem; border: 1px solid #eee; border-radius: 12px; transition: transform 0.2s, border-color 0.2s, box-shadow 0.2s;">
                    <a href="${item.getValue()}" style="text-decoration: none; color: #111; font-weight: 700; font-size: 1.2rem; display: block;">
                        ${item.getKey()} <span style="float: right; color: #d91b1b;">-></span>
                    </a>
                    <div style="font-size: 0.88rem; color: #64748b; margin-top: 0.45rem; line-height: 1.5;">
                        Open repair costs, common problems, and the next fix-or-sell question to check.
                    </div>
                </li>
            @endfor
        </ul>
        
        @if(items.isEmpty())
            <p style="color: #666;">No data available for this selection.</p>
        @endif
    </div>
`)
