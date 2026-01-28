@param String title
@param java.util.List<String> breadcrumbs
@param java.util.List<java.util.Map.Entry<String, String>> items

@template.layout(content = @`
    <div class="static-page-container" style="max-width: 800px; margin: 0 auto; padding: 4rem 1rem;">
        
        <!-- Breadcrumbs -->
        <div style="margin-bottom: 2rem; color: #666; font-size: 0.9rem;">
            <a href="/" style="color: #666; text-decoration: none;">Home</a>
            @for(String crumb : breadcrumbs)
                <span> / </span> <span>${crumb}</span>
            @endfor
        </div>

        <h1 style="font-size: 2.5rem; font-weight: 800; margin-bottom: 2rem;">${title}</h1>

        <ul style="list-style: none; padding: 0;">
            @for(java.util.Map.Entry<String, String> item : items)
                <li style="margin-bottom: 1rem; padding: 1rem; border: 1px solid #eee; border-radius: 8px; transition: transform 0.2s;">
                    <a href="${item.getValue()}" style="text-decoration: none; color: #111; font-weight: 700; font-size: 1.2rem; display: block;">
                        ${item.getKey()} <span style="float: right; color: #d91b1b;">→</span>
                    </a>
                </li>
            @endfor
        </ul>
        
        @if(items.isEmpty())
            <p style="color: #666;">No data available for this selection.</p>
        @endif
    </div>
`)
