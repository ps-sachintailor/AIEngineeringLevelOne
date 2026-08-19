<script>
  const tabs = [
    {
      id: 'chat',
      label: 'Chat',
      endpoint: 'POST /api/v1/chat',
      description: 'Sends a prompt directly to the configured chat model. This diagnostic endpoint does not search embedded documents.'
    },
    {
      id: 'documents',
      label: 'Embed document',
      endpoint: 'POST /api/v1/documents',
      description: 'Creates an embedding for the document and stores it in the in-memory vector store. The data is cleared when the server restarts.'
    },
    {
      id: 'ask',
      label: 'Ask documents',
      endpoint: 'POST /ask',
      description: 'Embeds the question, searches for the three most similar documents, and sends that retrieved context to the chat model.'
    }
  ];

  let activeTab = $state('chat');
  let chatMessage = $state('Explain retrieval-augmented generation briefly.');
  let documentContent = $state('Project Alpha uses access code BLUE-789.');
  let documentSource = $state('home-page');
  let question = $state('What access code does Project Alpha use?');
  let loading = $state(false);
  let result = $state(null);
  let error = $state('');

  const selectedTab = $derived(tabs.find((tab) => tab.id === activeTab));

  function selectTab(tabId) {
    activeTab = tabId;
    result = null;
    error = '';
  }

  async function callApi(path, body) {
    loading = true;
    result = null;
    error = '';

    try {
      const response = await fetch(path, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
      const payload = await response.json();

      if (!response.ok) {
        throw new Error(payload.message || `Request failed with status ${response.status}`);
      }

      result = payload;
    } catch (requestError) {
      error = requestError instanceof Error ? requestError.message : 'Request failed';
    } finally {
      loading = false;
    }
  }

  function submitChat(event) {
    event.preventDefault();
    callApi('/api/v1/chat', { message: chatMessage });
  }

  function submitDocument(event) {
    event.preventDefault();
    const metadata = documentSource.trim() ? { source: documentSource.trim() } : {};
    callApi('/api/v1/documents', { content: documentContent, metadata });
  }

  function submitQuestion(event) {
    event.preventDefault();
    callApi('/ask', { question });
  }
</script>

<svelte:head>
  <title>AI Engineering Level One</title>
</svelte:head>

<main>
  <header class="page-header">
    <p class="eyebrow">Spring Boot + Svelte 5</p>
    <h1>RAG API workspace</h1>
    <p>Try the three application endpoints from one simple page.</p>
  </header>

  <section class="workspace" aria-label="API workspace">
    <div class="tabs" role="tablist" aria-label="Application endpoints">
      {#each tabs as tab}
        <button
          id={`tab-${tab.id}`}
          type="button"
          role="tab"
          aria-selected={activeTab === tab.id}
          aria-controls={`panel-${tab.id}`}
          class:active={activeTab === tab.id}
          onclick={() => selectTab(tab.id)}
        >
          {tab.label}
        </button>
      {/each}
    </div>

    <div class="endpoint-summary">
      <code>{selectedTab.endpoint}</code>
      <p>{selectedTab.description}</p>
    </div>

    {#if activeTab === 'chat'}
      <div id="panel-chat" role="tabpanel" aria-labelledby="tab-chat">
        <form onsubmit={submitChat}>
          <label for="chat-message">Message</label>
          <textarea id="chat-message" bind:value={chatMessage} rows="5" required maxlength="4000"></textarea>
          <button class="primary" type="submit" disabled={loading}>{loading ? 'Sending…' : 'Send to chat'}</button>
        </form>
      </div>
    {:else if activeTab === 'documents'}
      <div id="panel-documents" role="tabpanel" aria-labelledby="tab-documents">
        <form onsubmit={submitDocument}>
          <label for="document-content">Document content</label>
          <textarea id="document-content" bind:value={documentContent} rows="7" required maxlength="20000"></textarea>

          <label for="document-source">Metadata source <span>(optional)</span></label>
          <input id="document-source" bind:value={documentSource} maxlength="200" />

          <button class="primary" type="submit" disabled={loading}>{loading ? 'Embedding…' : 'Embed document'}</button>
        </form>
      </div>
    {:else}
      <div id="panel-ask" role="tabpanel" aria-labelledby="tab-ask">
        <form onsubmit={submitQuestion}>
          <label for="question">Question</label>
          <textarea id="question" bind:value={question} rows="5" required maxlength="4000"></textarea>
          <p class="hint">Embed a related document first so the answer can use retrieved context.</p>
          <button class="primary" type="submit" disabled={loading}>{loading ? 'Searching…' : 'Search and ask'}</button>
        </form>
      </div>
    {/if}

    <section class="response" aria-live="polite" aria-label="API response">
      <h2>Response</h2>
      {#if error}
        <p class="error">{error}</p>
      {:else if result}
        <pre>{JSON.stringify(result, null, 2)}</pre>
      {:else}
        <p class="empty">The endpoint response will appear here.</p>
      {/if}
    </section>
  </section>
</main>
