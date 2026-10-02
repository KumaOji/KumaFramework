'use strict';
// Decode display escapes only inside JSON strings. A literal "\\n" remains literal.
function formatResponse(body, multiline = true) {
  let formatted;
  try { formatted = JSON.stringify(JSON.parse(body), null, 2); }
  catch { return body; }
  if (!multiline) return formatted;
  return formatted.replace(/"(?:\\.|[^"\\])*"/g, token =>
    token.replace(/\\(?:["\\/bfnrt]|u[0-9a-fA-F]{4})/g, escape =>
      ({'\\n':'\n', '\\r':'\r', '\\t':'\t'})[escape] ?? escape));
}
if (typeof module !== 'undefined') module.exports = {formatResponse};
